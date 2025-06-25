package com.example.demo.dao;

public final class UserMapper {

    private UserMapper() {
        // Prevent instantiation
    }

    public static UserDto toDto(User user) {
        if (user == null) return null;
        Long id = user.getId();
        String name = user.getName() != null ? user.getName() : "";
        String email = user.getEmail() != null ? user.getEmail() : "";

        return new UserDto(id, name, email);
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) return null;
        String name = dto.name() != null ? dto.name() : "";
        String email = dto.email() != null ? dto.email() : "";

        return User.builder()
                .id(dto.id())
                .name(name)
                .email(email)
                .build();
    }
}