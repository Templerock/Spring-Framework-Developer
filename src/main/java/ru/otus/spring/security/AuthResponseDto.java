package ru.otus.spring.security;

import ru.otus.spring.dto.UserDto;

public record AuthResponseDto(String token, UserDto user) {
}
