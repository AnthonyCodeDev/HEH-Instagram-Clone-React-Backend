package com.example.backendstragram.adapter.out.persistence.mapper;

import com.example.backendstragram.adapter.out.persistence.entity.UserEntity;
import com.example.backendstragram.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        return UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .build();
    }
}
