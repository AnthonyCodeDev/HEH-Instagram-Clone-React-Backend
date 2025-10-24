package be.heh.stragram.adapter.out.persistence.jpa.adapter;

import be.heh.stragram.adapter.out.persistence.jpa.SpringDataUserRepository;
import be.heh.stragram.adapter.out.persistence.jpa.entity.UserJpaEntity;
import be.heh.stragram.adapter.out.persistence.jpa.mapper.UserJpaMapper;
import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.LoadUserPort;
import be.heh.stragram.application.port.out.SaveUserPort;
import be.heh.stragram.application.port.out.SearchUsersPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements LoadUserPort, SaveUserPort, SearchUsersPort {

    private final SpringDataUserRepository userRepository;
    private final UserJpaMapper userMapper;

    @Override
    public Optional<User> findById(UserId id) {
        return userRepository.findById(id.getValue())
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserJpaEntity savedEntity = userRepository.save(userMapper.toEntity(user));
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public void delete(User user) {
        userRepository.deleteById(user.getId().getValue());
    }

    @Override
    public List<User> searchByUsernameOrBio(String query, int page, int size) {
        return userRepository.searchByUsernameOrBio(query, PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findRandomUsers(int size) {
        return userRepository.findRandomUsers(size)
                .stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }
}
