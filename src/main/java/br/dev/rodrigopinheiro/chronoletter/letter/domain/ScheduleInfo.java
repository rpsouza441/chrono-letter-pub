package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import br.dev.rodrigopinheiro.chronoletter.shared.domain.ValueObject;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.exception.InvalidScheduleDateException;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Timezone;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

public record ScheduleInfo(
        LocalDate scheduledDate,
        Timezone timezone,
        Instant sendAt,
        Integer sendHourLocalUsed) implements ValueObject {

    public static final int MAX_YEARS_IN_FUTURE = 10;

    public ScheduleInfo {
        validate(scheduledDate);
        Objects.requireNonNull(timezone, "Timezone cannot be null");
    }

    public static ScheduleInfo of(LocalDate scheduledDate, Timezone timezone) {
        return new ScheduleInfo(scheduledDate, timezone, null, null);
    }

    public static ScheduleInfo reconstitute(
            LocalDate scheduledDate,
            Timezone timezone,
            Instant sendAt,
            Integer sendHourLocalUsed) {
        Objects.requireNonNull(timezone, "Timezone cannot be null");
        // Cria diretamente sem validar data (é reconstituição)
        return new ScheduleInfo(scheduledDate, timezone, sendAt, sendHourLocalUsed);
    }

    public ScheduleInfo materialize(int sendHourLocal) {
        if (this.sendAt != null) {
            throw new IllegalStateException("ScheduleInfo already materialized");
        }
        ZonedDateTime sendDateTime = ZonedDateTime.of(
                this.scheduledDate,
                LocalTime.of(sendHourLocal, 0),
                this.timezone.zoneId());

        return new ScheduleInfo(
                this.scheduledDate,
                this.timezone,
                sendDateTime.toInstant(),
                sendHourLocal);
    }

    public boolean isMaterialized() {
        return this.sendAt != null;
    }

    private static void validate(LocalDate scheduledDate) {
        if (scheduledDate == null) {
            throw new InvalidScheduleDateException("Scheduled date cannot be null");
        }

        LocalDate today = LocalDate.now();

        if (!scheduledDate.isAfter(today)) {
            throw new InvalidScheduleDateException(
                    "Scheduled date must be in the future");
        }

        LocalDate maxDate = today.plusYears(MAX_YEARS_IN_FUTURE);

        if (scheduledDate.isAfter(maxDate)) {
            throw new InvalidScheduleDateException(
                    "Scheduled date cannot be more than " + MAX_YEARS_IN_FUTURE + " years in the future");
        }
    }
}
