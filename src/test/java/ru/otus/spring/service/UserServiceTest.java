package ru.otus.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void addCreatesUserWithEncodedPassword() {
        var added = userService.add("reader", "secret");

        assertThat(added).isPresent();
        assertThat(added.get().username()).isEqualTo("reader");
        var stored = userService.getById(added.get().id());
        assertThat(stored).isPresent();
        assertThat(stored.get().password()).isNull();
    }

    @Test
    void addReturnsEmptyForDuplicateUsername() {
        userService.add("duplicate", "pass");

        assertThat(userService.add("duplicate", "other")).isEmpty();
    }

    @Test
    void loadingUnknownUserFails() {
        assertThat(userService.getById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void loadUserByUsernameReturnsUserDetailsWithRole() {
        userService.add("reader", "secret");

        UserDetails details = userService.loadUserByUsername("reader");

        assertThat(details.getUsername()).isEqualTo("reader");
        assertThat(details.getPassword()).isNotEqualTo("secret");
        assertThat(passwordEncoder.matches("secret", details.getPassword())).isTrue();
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }
}
