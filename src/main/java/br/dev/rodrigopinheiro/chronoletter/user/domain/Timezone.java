package br.dev.rodrigopinheiro.chronoletter.user.domain;

import java.time.ZoneId;

import br.dev.rodrigopinheiro.chronoletter.shared.domain.ValueObject;
import br.dev.rodrigopinheiro.chronoletter.user.domain.exception.InvalidTimezoneException;

public record Timezone(String value) implements ValueObject {

    private static final String DEFAULT_TIMEZONE = "America/Sao_Paulo";

    public Timezone {
        if (value == null) {
            throw new InvalidTimezoneException(null);
        }
        try {
            ZoneId.of(value);
        } catch (Exception e) {
            throw new InvalidTimezoneException(value, e);
        }
    }

    public static Timezone of(String value) {
        return new Timezone(value);
    }

    public static Timezone ofDefaultTimezone() {
        return new Timezone(DEFAULT_TIMEZONE);
    }

    public ZoneId zoneId() {
        return ZoneId.of(value);
    }

}
