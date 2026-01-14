package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.DeliveryAttemptStatus;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.DeliveryProvider;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class DeliveryAttemptTest {

    private static final LetterId LETTER_ID = LetterId.generate();

    @Test
    public void shouldCreateNewAttempt() {

        var attempt = DeliveryAttempt.create(LETTER_ID, 1, DeliveryProvider.SMTP);

        assertThat(attempt.getLetterId()).isEqualTo(LETTER_ID);
        assertThat(attempt.getAttemptNumber()).isEqualTo(1);
        assertThat(attempt.getStatus()).isEqualTo(DeliveryAttemptStatus.PENDING);
        assertThat(attempt.getProvider()).isEqualTo(DeliveryProvider.SMTP);
        assertThat(attempt.getStartedAt()).isNotNull();

    }

    @Test
    public void shouldMarkAsSuccess() {
        var attempt = DeliveryAttempt.create(LETTER_ID, 1, DeliveryProvider.SMTP);
        attempt.markAsSuccess("Message-ID: abc123");

        assertThat(attempt.getStatus()).isEqualTo(DeliveryAttemptStatus.SUCCESS);
        assertThat(attempt.getProviderResponse()).isEqualTo("Message-ID: abc123");
        assertThat(attempt.getCompletedAt()).isNotNull();
    }

    @Test
    public void shouldMarkAsFailed() {
        var attempt = DeliveryAttempt.create(LETTER_ID, 1, DeliveryProvider.SMTP);
        attempt.markAsFailed("SMTP_TIMEOUT", "Connection timed out after 30s");

        assertThat(attempt.getStatus()).isEqualTo(DeliveryAttemptStatus.FAILED);
        assertThat(attempt.getErrorCode()).isEqualTo("SMTP_TIMEOUT");
        assertThat(attempt.getErrorMessage()).isEqualTo("Connection timed out after 30s");
        assertThat(attempt.getCompletedAt()).isNotNull();
    }

    @Test
    public void shouldMarkAsRetrying() {
        var attempt = DeliveryAttempt.create(LETTER_ID, 1, DeliveryProvider.SMTP);
        attempt.markAsRetrying("RATE_LIMITED", "Too many requests");

        assertThat(attempt.getStatus()).isEqualTo(DeliveryAttemptStatus.RETRYING);
        assertThat(attempt.getErrorCode()).isEqualTo("RATE_LIMITED");
        assertThat(attempt.getErrorMessage()).isEqualTo("Too many requests");
        assertThat(attempt.getCompletedAt()).isNotNull();
    }

}
