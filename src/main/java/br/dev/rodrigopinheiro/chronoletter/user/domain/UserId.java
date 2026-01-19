package br.dev.rodrigopinheiro.chronoletter.user.domain;

import java.util.Objects;
import java.util.UUID;

import br.dev.rodrigopinheiro.chronoletter.shared.domain.ValueObject;

public record UserId(
        UUID value) implements ValueObject {

    public UserId {
        Objects.requireNonNull(value, "UserId value cannot be null");
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId from(UUID value) {
        return new UserId(value);
    }

    public static UserId of(String uuid) {
        try {
            return new UserId(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + uuid, e);
        }
    }

    public static UserId of(UUID uuid) {
        return new UserId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
