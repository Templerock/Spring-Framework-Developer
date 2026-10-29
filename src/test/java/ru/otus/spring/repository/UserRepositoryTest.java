package ru.otus.spring.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.spring.domain.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void seededUsersExist() {
        assertThat(userRepository.findByUsername("admin")).isPresent();
        assertThat(userRepository.findByUsername("user")).isPresent();
    }

    @Test
    void savedUserCanBeReadById() {
        var user = userRepository.save(new User(0, "reader", "hash", "ROLE_USER"));

        assertThat(user.getId()).isPositive();
        assertThat(userRepository.findById(user.getId()))
                .get()
                .extracting(User::getUsername, User::getRole)
                .containsExactly("reader", "ROLE_USER");
    }

    @Test
    void findByUsernameReturnsUserByExactName() {
        var user = userRepository.save(new User(0, "reader", "hash", "ROLE_USER"));

        assertThat(userRepository.findByUsername("reader"))
                .get()
                .extracting(User::getId)
                .isEqualTo(user.getId());
    }

    @Test
    void findByUsernameReturnsEmptyForUnknownName() {
        assertThat(userRepository.findByUsername("nobody")).isEmpty();
    }

    @Test
    void updateChangesUsernameAndPassword() {
        var user = userRepository.save(new User(0, "old", "oldHash", "ROLE_USER"));

        userRepository.save(new User(user.getId(), "new", "newHash", "ROLE_ADMIN"));

        assertThat(userRepository.findById(user.getId()))
                .get()
                .extracting(User::getUsername, User::getPassword, User::getRole)
                .containsExactly("new", "newHash", "ROLE_ADMIN");
    }

    @Test
    void deleteRemovesUser() {
        var user = userRepository.save(new User(0, "doomed", "hash", "ROLE_USER"));

        userRepository.delete(user);

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void countMatchesInsertions() {
        long before = userRepository.count();

        userRepository.save(new User(0, "one" + before, "hash", "ROLE_USER"));

        assertThat(userRepository.count()).isEqualTo(before + 1);
    }
}
