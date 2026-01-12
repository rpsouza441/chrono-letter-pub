package br.dev.rodrigopinheiro.chronoletter.user.domain;

import br.dev.rodrigopinheiro.chronoletter.user.domain.exception.InvalidEmailException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;


public class EmailTest {


    @Test   
    public void shouldCreateValidEmail() {
        Email email = Email.of("user@example.com");
        
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    public void shouldNormalizeToLowerCase() {
        Email email = Email.of("USER@EXAMPLE.COM");
        
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @Test
    public void shouldTrimWhitespace() {
        Email email = Email.of(" user@example.com ");
        
        assertThat(email.value()).isEqualTo("user@example.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "no-at-sign", "@nodomain", "no@domain", ""})
    public void shouldRejectInvalidEmail(String invalidEmail) {
        assertThatThrownBy(() -> Email.of(invalidEmail))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    public void shouldRejectNullEmail() {
        assertThatThrownBy(() -> Email.of(null))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    public void shouldbeEqualWhenSameValue() {
        Email email1 = Email.of("user@example.com");
        Email email2 = Email.of("USER@example.com");  // normalizado
        
        assertThat(email1).isEqualTo(email2);
    }

    @Test
    public void shouldExtractLocalPart() {
        Email email = Email.of("user@example.com");
        
        assertThat(email.localPart()).isEqualTo("user");
    }

    @Test
    public void shouldExtractDomain() {
        Email email = Email.of("user@example.com");
        
        assertThat(email.domain()).isEqualTo("example.com");
    }
}
