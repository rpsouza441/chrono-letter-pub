package br.dev.rodrigopinheiro.chronoletter.user.domain;

import org.junit.jupiter.api.Test;

import br.dev.rodrigopinheiro.chronoletter.user.domain.exception.InvalidTimezoneException;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;

public class TimezoneTest {

    @Test
    public void shouldCreateValidTimezone() {
        Timezone timezone = Timezone.of("America/Sao_Paulo");

        assertThat(timezone.value()).isEqualTo("America/Sao_Paulo");
        assertThat(timezone.zoneId()).isEqualTo(ZoneId.of("America/Sao_Paulo"));
    }

    @Test
    public void shouldProvideDefaultTimezone() {
        Timezone timezone = Timezone.ofDefaultTimezone();

        assertThat(timezone.value()).isEqualTo("America/Sao_Paulo");
        assertThat(timezone.zoneId()).isEqualTo(ZoneId.of("America/Sao_Paulo"));
    }

    @Test
    public void shouldRejectInvalidTimezone() {
        assertThatThrownBy(() -> Timezone.of("invalid/timezone"))
                .isInstanceOf(InvalidTimezoneException.class);
    }

    @Test
    public void shouldRejectNullTimezone() {
        assertThatThrownBy(() -> Timezone.of(null))
                .isInstanceOf(InvalidTimezoneException.class);
    }

    @Test
    public void shouldBeEqualWhenSameZone() {
        Timezone timezone1 = Timezone.of("America/Sao_Paulo");
        Timezone timezone2 = Timezone.of("America/Sao_Paulo");

        assertThat(timezone1).isEqualTo(timezone2);
    }

}
