package com.example.demo.dao;

public class UserMapper {
    public static UserDto toDto(User user) {
        if (user == null) return null;

        return new UserDto(user.getName(), user.getEmail());
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) return null;

        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .build();
    }
}