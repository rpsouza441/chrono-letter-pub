package br.dev.rodrigopinheiro.chronoletter.user.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

public class UserIdTest {

    @Test
    public void shouldGenerateUserId() {
        UserId userId = UserId.generate();

        assertThat(userId).isNotNull();
        assertThat(userId.value()).isNotNull();

    }

    @Test
    public void shouldCreateFromExistingUuid() {
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.from(uuid);

        assertThat(userId.value()).isEqualTo(uuid);
    }

    @Test
    public void shouldCreateFromString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        UserId userId = UserId.of(uuidString);

        assertThat(userId.value().toString()).isEqualTo(uuidString);
    }

    @Test
    public void shouldBeEqualWhenSameUuid() {
        UUID uuid = UUID.randomUUID();
        UserId userId1 = UserId.from(uuid);
        UserId userId2 = UserId.from(uuid);

        assertThat(userId1).isEqualTo(userId2);
        assertThat(userId1.hashCode()).isEqualTo(userId2.hashCode());
    }

    @Test
    public void shouldRejectNullUuid() {
        assertThatThrownBy(() -> UserId.from(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldRejectInvalidUuidString() {
        assertThatThrownBy(() -> UserId.of("not-a-uuid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
