package ru.otus.spring.dto;

import ru.otus.spring.domain.User;

public record UserDto(Long id, String username, String password, String role) {

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername(), null, user.getRole());
    }
}
