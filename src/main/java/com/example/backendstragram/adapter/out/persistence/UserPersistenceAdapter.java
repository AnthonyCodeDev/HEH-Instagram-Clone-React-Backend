package com.example.backendstragram.adapter.out.persistence;

import com.example.backendstragram.adapter.out.persistence.entity.UserEntity;
import com.example.backendstragram.adapter.out.persistence.mapper.UserMapper;
import com.example.backendstragram.application.ports.out.UserRepositoryPort;
import com.example.backendstragram.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toDomain);
    }
}
