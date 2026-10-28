package ru.otus.spring.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.domain.User;
import ru.otus.spring.dto.UserDto;
import ru.otus.spring.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserDto> add(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return Optional.empty();
        }
        return Optional.of(UserDto.from(userRepository.save(new User(0, username, passwordEncoder.encode(password), "ROLE_USER"))));
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    public Optional<UserDto> getById(long id) {
        return userRepository.findById(id).map(UserDto::from);
    }

    public Optional<UserDto> update(long id, String username, String password) {
        var existing = userRepository.findById(id);
        if (existing.isEmpty() || userRepository.findByUsername(username).map(User::getId).filter(other -> other != id).isPresent()) {
            return Optional.empty();
        }
        var user = existing.get();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return Optional.of(UserDto.from(user));
    }

    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().startsWith("ROLE_") ? user.getRole().substring("ROLE_".length()) : user.getRole())
                .build();
    }
}
