package br.dev.rodrigopinheiro.chronoletter.user.infrastructure.adapter.out.persistence;

import br.dev.rodrigopinheiro.chronoletter.user.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(UserRepositoryAdapter.class)
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindUserById() {
        // Given
        User user = User.register(
                Email.of("test@example.com"),
                Timezone.ofDefaultTimezone());

        // When
        userRepositoryAdapter.save(user);
        var found = userRepositoryAdapter.findById(user.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(user.getId());
        assertThat(found.get().getEmail().value()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = User.register(
                Email.of("findme@example.com"),
                Timezone.ofDefaultTimezone());
        userRepositoryAdapter.save(user);

        // When
        var found = userRepositoryAdapter.findByEmail(Email.of("findme@example.com"));

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Given
        User user = User.register(
                Email.of("exists@example.com"),
                Timezone.ofDefaultTimezone());
        userRepositoryAdapter.save(user);

        // When & Then
        assertThat(userRepositoryAdapter.existsByEmail(Email.of("exists@example.com"))).isTrue();
        assertThat(userRepositoryAdapter.existsByEmail(Email.of("notexists@example.com"))).isFalse();
    }

    @Test
    void shouldDeleteUser() {
        // Given
        User user = User.register(
                Email.of("delete@example.com"),
                Timezone.ofDefaultTimezone());
        userRepositoryAdapter.save(user);

        // When
        userRepositoryAdapter.delete(user);

        // Then
        assertThat(userRepositoryAdapter.findById(user.getId())).isEmpty();
    }
}
