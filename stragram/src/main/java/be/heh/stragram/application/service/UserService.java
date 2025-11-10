package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.exception.NotFoundException;
import be.heh.stragram.application.domain.exception.UnauthorizedActionException;
import be.heh.stragram.application.domain.exception.ValidationException;
import be.heh.stragram.application.domain.model.Notification;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.model.follow.FollowRelationship;
import be.heh.stragram.application.domain.service.FollowDomainService;
import be.heh.stragram.application.domain.value.Email;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.domain.value.Username;
import be.heh.stragram.application.port.in.*;
import be.heh.stragram.application.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements GetUserProfileQuery, UpdateUserProfileCommand, DeleteUserCommand,
    SearchUsersQuery, FollowUserUseCase, UnfollowUserUseCase, be.heh.stragram.application.port.in.ListRandomUsersQuery,
    be.heh.stragram.application.port.in.ChangePasswordUseCase {

    // Also implement random listing
    // Will delegate to SearchUsersPort.findRandomUsers

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final SearchUsersPort searchUsersPort;
    private final FollowPort followPort;
    private final NotificationPort notificationPort;
    private final FollowDomainService followDomainService;
    private final be.heh.stragram.application.port.out.PasswordEncoderPort passwordEncoderPort;


    @Override
    @Transactional(readOnly = true)
    public User getUserById(UserId userId) {
        return loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return loadUserPort.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", username));
    }

    @Override
    @Transactional
    public User update(UserId userId, UpdateProfileCommand command) {
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        // Validate username if changed
        if (!user.getUsername().toString().equals(command.getUsername()) && 
                loadUserPort.existsByUsername(command.getUsername())) {
            throw new ValidationException("Username is already taken");
        }

        // Validate email if changed
        if (!user.getEmail().toString().equals(command.getEmail()) && 
                loadUserPort.existsByEmail(command.getEmail())) {
            throw new ValidationException("Email is already taken");
        }

        Username username = Username.of(command.getUsername());
        Email email = Email.of(command.getEmail());
        
        // Extract social links from map
        String tiktok = null, twitter = null, youtube = null;
        if (command.getSocialLinks() != null) {
            tiktok = command.getSocialLinks().get("tiktok");
            twitter = command.getSocialLinks().get("twitter");
            youtube = command.getSocialLinks().get("youtube");
        }
        
        user.updateProfile(username, email, command.getBio(), command.getAvatarUrl(),
                command.getName(), command.getBannerUrl(), command.getPhone(), command.getLocation(),
                command.getBirthdate(), tiktok, twitter, youtube);
        user.validateBioLength(500);
        
        return saveUserPort.save(user);
    }

    @Override
    @Transactional
    public void delete(UserId userId, UserId requesterId) {
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));
        
        User requester = loadUserPort.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User", requesterId.toString()));

        // Only the user themselves or an admin can delete a user
        if (!userId.equals(requesterId) && !requester.isAdmin()) {
            throw new UnauthorizedActionException("You don't have permission to delete this user");
        }

        saveUserPort.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> search(String query, int page, int size) {
        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("Search query cannot be empty");
        }
        
        return searchUsersPort.searchByUsernameOrBio(query.trim(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listRandomUsers(int size) {
        if (size <= 0) {
            return java.util.Collections.emptyList();
        }

        return searchUsersPort.findRandomUsers(size);
    }

    @Override
    @Transactional
    public FollowRelationship follow(UserId followerId, UserId targetId) {
        User follower = loadUserPort.findById(followerId)
                .orElseThrow(() -> new NotFoundException("User", followerId.toString()));
        
        User target = loadUserPort.findById(targetId)
                .orElseThrow(() -> new NotFoundException("User", targetId.toString()));

        // Check if already following
        if (followPort.exists(followerId, targetId)) {
            throw new ValidationException("Already following this user");
        }

        // Create follow relationship
        FollowRelationship followRelationship = followDomainService.createFollowRelationship(follower, target);
        
        // Save updated users
        saveUserPort.save(follower);
        saveUserPort.save(target);
        
        // Save follow relationship
        FollowRelationship savedRelationship = followPort.save(followRelationship);
        
        // Create notification for the target user
        Map<String, String> payload = new HashMap<>();
        payload.put("followerId", followerId.toString());
        payload.put("followerUsername", follower.getUsername().toString());
        
        Notification notification = Notification.create(
                target.getId(),
                Notification.NotificationType.FOLLOW,
                payload
        );
        
        notificationPort.save(notification);
        
        return savedRelationship;
    }

    @Override
    @Transactional
    public void unfollow(UserId followerId, UserId targetId) {
        User follower = loadUserPort.findById(followerId)
                .orElseThrow(() -> new NotFoundException("User", followerId.toString()));
        
        User target = loadUserPort.findById(targetId)
                .orElseThrow(() -> new NotFoundException("User", targetId.toString()));

        // Check if following
        if (!followPort.exists(followerId, targetId)) {
            throw new ValidationException("Not following this user");
        }

        // Remove follow relationship
        followDomainService.removeFollowRelationship(follower, target);
        
        // Save updated users
        saveUserPort.save(follower);
        saveUserPort.save(target);
        
        // Delete follow relationship
        followPort.delete(followerId, targetId);
    }

    @Override
    @Transactional
    public void changePassword(UserId userId, ChangePasswordUseCase.ChangePasswordCommand command) {
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        // Verify current password
        if (!passwordEncoderPort.matches(command.getCurrentPassword(), user.getPasswordHash())) {
            throw new UnauthorizedActionException("Current password is incorrect");
        }

        // Basic validation for new password
        if (command.getNewPassword() == null || command.getNewPassword().length() < 8) {
            throw new ValidationException("New password must be at least 8 characters long");
        }

        be.heh.stragram.application.domain.value.PasswordHash newHash = passwordEncoderPort.encode(command.getNewPassword());
        user.changePassword(newHash);
        saveUserPort.save(user);
    }
}
