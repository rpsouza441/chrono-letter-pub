package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import java.util.Objects;
import java.util.UUID;

import br.dev.rodrigopinheiro.chronoletter.shared.domain.ValueObject;

public record LetterId(UUID value) implements ValueObject {

    public LetterId {
        Objects.requireNonNull(value, "LetterId value cannot be null");
    }

    public static LetterId generate() {
        return new LetterId(UUID.randomUUID());
    }

    public static LetterId of(UUID value) {
        return new LetterId(value);
    }

    public static LetterId of(String value) {
        try {
            return new LetterId(UUID.fromString(value));

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID string: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
