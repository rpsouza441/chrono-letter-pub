package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.exception.InvalidScheduleDateException;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Timezone;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import static org.assertj.core.api.Assertions.*;

public class ScheduleInfoTest {
    private static final Timezone SAO_PAULO = Timezone.of("America/Sao_Paulo");


    @Test
    public void shouldAcceptTomorrowAsScheduleDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ScheduleInfo schedule = ScheduleInfo.of(tomorrow, SAO_PAULO);

        assertThat(schedule.scheduledDate()).isEqualTo(tomorrow);
        assertThat(schedule.timezone()).isEqualTo(SAO_PAULO);
    }

    @Test
    public void shouldRejectPastDate() {
        LocalDate pastDate = LocalDate.now().minusDays(1);

        assertThatThrownBy(() -> ScheduleInfo.of(pastDate, SAO_PAULO))
                .isInstanceOf(InvalidScheduleDateException.class)
                .hasMessageContaining("in the future");
    }

    @Test
    public void shouldRejectTodayAsScheduleDate() {
        LocalDate today = LocalDate.now();

        assertThatThrownBy(() -> ScheduleInfo.of(today, SAO_PAULO))
                .isInstanceOf(InvalidScheduleDateException.class)
                .hasMessageContaining("in the future");
    }

    @Test
    public void shouldRejectNullDate() {

        LocalDate futureDate = LocalDate.now().plusDays(30);

        assertThatThrownBy(() -> ScheduleInfo.of(futureDate, null))
                .isInstanceOf(NullPointerException.class);

    }

    @Test
    public void shouldMaterializeSendAt() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        ScheduleInfo schedule = ScheduleInfo.of(futureDate, SAO_PAULO);

        int sendHour = 10;
        ScheduleInfo materialized = schedule.materialize(sendHour);

        assertThat(materialized).isNotNull();
        assertThat(materialized.sendHourLocalUsed()).isEqualTo(sendHour);

        // Verifica que a hora é 10:00 no timezone correto
        ZonedDateTime sendAtZoned = materialized.sendAt()
                .atZone(ZoneId.of("America/Sao_Paulo"));
        assertThat(sendAtZoned.getHour()).isEqualTo(10);
        assertThat(sendAtZoned.getMinute()).isEqualTo(0);
    }

    @Test
    public void shouldNotMaterializeTwice() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        ScheduleInfo schedule = ScheduleInfo.of(futureDate, SAO_PAULO);

        int sendHour = 10;
        ScheduleInfo materialized = schedule.materialize(sendHour);

        assertThatThrownBy(() -> materialized.materialize(11))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already materialized");
    }

    @Test
    public void shouldBeEqualWhenSameDateAndTimezone() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        ScheduleInfo schedule1 = ScheduleInfo.of(futureDate, SAO_PAULO);
        ScheduleInfo schedule2 = ScheduleInfo.of(futureDate, SAO_PAULO);

        assertThat(schedule1).isEqualTo(schedule2);
        assertThat(schedule1).isEqualTo(schedule2);
    }

    @Test
    public void shouldCreateValidScheduleInfo() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        ScheduleInfo schedule = ScheduleInfo.of(futureDate, SAO_PAULO);
        
        assertThat(schedule.scheduledDate()).isEqualTo(futureDate);
        assertThat(schedule.timezone()).isEqualTo(SAO_PAULO);
    }
    
}