package ru.otus.spring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.spring.dto.UserDto;
import ru.otus.spring.repository.UserRepository;
import ru.otus.spring.security.AuthResponseDto;
import ru.otus.spring.security.JwtService;
import ru.otus.spring.security.LoginDto;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserDetailsService userDetailsService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto login) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.username(), login.password()));
        var userDetails = userDetailsService.loadUserByUsername(login.username());
        var user = userRepository.findByUsername(login.username())
                .map(UserDto::from)
                .orElseThrow();
        return ResponseEntity.ok(new AuthResponseDto(jwtService.generateToken(userDetails), user));
    }
}
