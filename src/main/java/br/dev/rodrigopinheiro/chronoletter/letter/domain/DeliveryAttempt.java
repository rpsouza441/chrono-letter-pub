package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.DeliveryAttemptStatus;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.DeliveryProvider;

/**
 * Entity que rastreia cada tentativa de envio de uma carta.
 * Associada ao aggregate Letter.
 */
public class DeliveryAttempt {

    private final UUID id;
    private final LetterId letterId;
    private final int attemptNumber;
    private final DeliveryProvider provider;
    private final Instant startedAt;

    private DeliveryAttemptStatus status;
    private String errorCode;
    private String errorMessage;
    private String providerResponse;
    private Instant completedAt;

    private DeliveryAttempt(
            UUID id,
            LetterId letterId,
            int attemptNumber,
            DeliveryProvider provider,
            Instant startedAt) {
        this.id = id;
        this.letterId = Objects.requireNonNull(letterId);
        this.attemptNumber = attemptNumber;
        this.provider = Objects.requireNonNull(provider);
        this.startedAt = startedAt;
        this.status = DeliveryAttemptStatus.PENDING;
    }

    public static DeliveryAttempt create(LetterId letterId, int attemptNumber, DeliveryProvider provider) {
        return new DeliveryAttempt(
                UUID.randomUUID(),
                letterId,
                attemptNumber,
                provider,
                Instant.now());
    }

    public static DeliveryAttempt reconstitute(
            UUID id,
            LetterId letterId,
            int attemptNumber,
            DeliveryProvider provider,
            DeliveryAttemptStatus status,
            String errorCode,
            String errorMessage,
            String providerResponse,
            Instant startedAt,
            Instant completedAt) {
        var attempt = new DeliveryAttempt(id, letterId, attemptNumber, provider, startedAt);
        attempt.status = status;
        attempt.errorCode = errorCode;
        attempt.errorMessage = errorMessage;
        attempt.providerResponse = providerResponse;
        attempt.completedAt = completedAt;
        return attempt;
    }

    public void markAsSuccess(String providerResponse) {
        this.status = DeliveryAttemptStatus.SUCCESS;
        this.providerResponse = providerResponse;
        this.completedAt = Instant.now();
    }

    public void markAsFailed(String errorCode, String errorMessage) {
        this.status = DeliveryAttemptStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
    }

    public void markAsRetrying(String errorCode, String errorMessage) {
        this.status = DeliveryAttemptStatus.RETRYING;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public LetterId getLetterId() {
        return letterId;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public DeliveryProvider getProvider() {
        return provider;
    }

    public DeliveryAttemptStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getProviderResponse() {
        return providerResponse;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

}
