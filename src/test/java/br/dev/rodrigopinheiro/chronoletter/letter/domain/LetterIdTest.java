package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

public class LetterIdTest {

    @Test
    public void shouldGenerateNewLetterId() {
        LetterId letterId = LetterId.generate();

        assertThat(letterId).isNotNull();
        assertThat(letterId.value()).isNotNull();
    }

    @Test
    public void shouldCreateLetterIdFromUUID() {
        UUID uuid = UUID.randomUUID();
        LetterId letterId = LetterId.of(uuid);

        assertThat(letterId).isNotNull();
        assertThat(letterId.value()).isEqualTo(uuid);
    }

    @Test
    public void shouldCreateFromUUIDString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        LetterId letterId = LetterId.of(uuidString);

        assertThat(letterId).isNotNull();
        assertThat(letterId.value().toString()).isEqualTo(uuidString);
    }

    @Test
    public void shouldBeEqualWhenSameUuid() {
        UUID uuid = UUID.randomUUID();
        LetterId letterId1 = LetterId.of(uuid);
        LetterId letterId2 = LetterId.of(uuid);

        assertThat(letterId1).isEqualTo(letterId2);
        assertThat(letterId1.hashCode()).isEqualTo(letterId2.hashCode());
    }

    @Test
    public void shouldRejectNullUuid() {
        assertThatThrownBy(() -> LetterId.of((UUID) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldRejectNullUuidString() {
        assertThatThrownBy(() -> LetterId.of((String) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldRejectInvalidUuidString() {
        assertThatThrownBy(() -> LetterId.of("invalid-uuid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
