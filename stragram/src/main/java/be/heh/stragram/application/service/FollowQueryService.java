package be.heh.stragram.application.service;

import be.heh.stragram.application.domain.model.User;
import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.in.ListFollowersQuery;
import be.heh.stragram.application.port.in.ListFollowingQuery;
import be.heh.stragram.application.port.out.FollowPort;
import be.heh.stragram.application.port.out.LoadUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowQueryService implements ListFollowersQuery, ListFollowingQuery {

    private final FollowPort followPort;
    private final LoadUserPort loadUserPort;

    @Override
    @Transactional(readOnly = true)
    public List<User> listFollowers(UserId userId, int page, int size) {
        // Get all follower IDs
        List<UserId> followerIds = followPort.findFollowerIds(userId, page, size);
        
        // Load full user objects for each follower
        return followerIds.stream()
                .map(loadUserPort::findById)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowers(UserId userId) {
        return followPort.countFollowers(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listFollowing(UserId userId, int page, int size) {
        // Get all following IDs
        List<UserId> followingIds = followPort.findFollowingIds(userId, page, size);
        
        // Load full user objects for each following
        return followingIds.stream()
                .map(loadUserPort::findById)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countFollowing(UserId userId) {
        return followPort.countFollowing(userId);
    }
}
