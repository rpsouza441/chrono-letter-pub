package br.dev.rodrigopinheiro.chronoletter.user.domain;

import br.dev.rodrigopinheiro.chronoletter.user.domain.exception.EmailAlreadyVerifiedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class UserTest {

     @Test
     public void shouldRegisterNewUserWithPendingVerification() {
          User user = User.register(
                    Email.of("teste@example.com"),
                    Timezone.ofDefaultTimezone());

          assertThat(user.getId()).isNotNull();
          assertThat(user.getEmail().value()).isEqualTo("teste@example.com");
          assertThat(user.getTimezone()).isEqualTo(Timezone.ofDefaultTimezone());
          assertThat(user.isEmailVerified()).isFalse();
          assertThat(user.getCreatedAt()).isNotNull();
     }

     @Test
     public void shouldVerifyUserEmail() {
          User user = User.register(
                    Email.of("teste@example.com"),
                    Timezone.ofDefaultTimezone());

          user.verifyEmail();

          assertThat(user.isEmailVerified()).isTrue();
     }

     @Test
     public void shouldNotVerifyEmailTwice() {
          User user = User.register(
                    Email.of("teste@example.com"),
                    Timezone.ofDefaultTimezone());

          user.verifyEmail();

          assertThatThrownBy(() -> user.verifyEmail())
                    .isInstanceOf(EmailAlreadyVerifiedException.class);
     }

     @Test
     public void shouldUpdateTimezone() {
          User user = User.register(
                    Email.of("teste@example.com"),
                    Timezone.of("America/Sao_Paulo"));
          var originalUpdatedAt = user.getUpdatedAt();
          user.changeTimezone(Timezone.of("America/New_York"));

          assertThat(user.getTimezone()).isEqualTo(Timezone.of("America/New_York"));
          assertThat(user.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
     }

     @Test
     public void shouldBeEqualWhenSameId() {
          UserId id = UserId.generate();
          User user1 = User.reconstitute(
                    id,
                    Email.of("a@example.com"),
                    true,
                    Timezone.ofDefaultTimezone(),
                    null, null, null, null, null);
          User user2 = User.reconstitute(
                    id,
                    Email.of("b@example.com"),
                    true,
                    Timezone.ofDefaultTimezone(),
                    null, null, null, null, null);

          assertThat(user1).isEqualTo(user2);
     }
}
