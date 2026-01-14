package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.exception.LetterAlreadySentException;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Timezone;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

public class LetterTest {
    private static final UserId OWNER_ID = UserId.generate();
    private static final LetterContent CONTENT = LetterContent.of("Querido eu do futuro, espero que esteja bem!");
    private static final Timezone TIMEZONE = Timezone.of("America/Sao_Paulo");

    private ScheduleInfo createSchedule() {
        return ScheduleInfo.of(LocalDate.now().plusDays(30), TIMEZONE);
    }

    @Test
    public void shouldCreateDraftLetter() {
        Letter letter = Letter.draft(OWNER_ID, CONTENT, createSchedule());

        assertThat(letter.getId()).isNotNull();
        assertThat(letter.getOwnerId()).isEqualTo(OWNER_ID);
        assertThat(letter.getContent()).isEqualTo(CONTENT);
        assertThat(letter.getStatus()).isEqualTo(LetterStatus.DRAFT);
        assertThat(letter.getCreatedAt()).isNotNull();
    }

    @Test
    public void shouldScheduleDraftLetter() {

        Letter letter = Letter.draft(OWNER_ID, CONTENT, createSchedule());

        letter.schedule();

        assertThat(letter.getStatus()).isEqualTo(LetterStatus.PENDING);
    }

    @Test
    public void shouldNotScheduleAlreadySentLetter() {

        Letter letter = Letter.reconstitute(
                LetterId.generate(),
                OWNER_ID,
                CONTENT,
                createSchedule(),
                LetterStatus.SENT,
                null, null, null, null);
        assertThatThrownBy(() -> letter.schedule())
                .isInstanceOf(LetterAlreadySentException.class);

    }

    @Test
    public void shouldUpdateContentWhileDraft() {

        Letter letter = Letter.draft(OWNER_ID, CONTENT, createSchedule());
        LetterContent newContent = LetterContent.of("Novo conteúdo da carta para o futuro.");
        letter.updateContent(newContent);
        assertThat(letter.getContent()).isEqualTo(newContent);
    }

    @Test
    public void shouldUpdateContentWhilePending() {

        Letter letter = Letter.pending(OWNER_ID, CONTENT, createSchedule());
        letter.schedule();
        LetterContent newContent = LetterContent.of("Novo conteúdo da carta para o futuro.");
        letter.updateContent(newContent);
        assertThat(letter.getContent()).isEqualTo(newContent);
    }

    @Test
    public void shouldNotUpdateContentAfterSent() {

        Letter letter = Letter.reconstitute(
                LetterId.generate(),
                OWNER_ID,
                CONTENT,
                createSchedule(),
                LetterStatus.SENT,
                null, null, null, null);
        LetterContent newContent = LetterContent.of("Novo conteúdo da carta para o futuro.");
        letter.updateContent(newContent);
        assertThatThrownBy(() -> letter.updateContent(newContent))
                .isInstanceOf(LetterAlreadySentException.class);
    }

    @Test
    public void shouldMarkAsSending() {

        Letter letter = Letter.pending(OWNER_ID, CONTENT, createSchedule());
        letter.schedule();
        letter.markAsSending();
        assertThat(letter.getStatus()).isEqualTo(LetterStatus.SENDING);
    }

    @Test
    public void shouldMarkAsSent() {

        Letter letter = Letter.pending(OWNER_ID, CONTENT, createSchedule());
        letter.schedule();
        letter.markAsSending();
        letter.markAsSent();
        assertThat(letter.getStatus()).isEqualTo(LetterStatus.SENT);
    }

    @Test
    public void shouldMarkAsFailed() {

        Letter letter = Letter.pending(OWNER_ID, CONTENT, createSchedule());
        letter.schedule();
        letter.markAsSending();
        letter.markAsFailed("SMTP connection timeout");
        assertThat(letter.getStatus()).isEqualTo(LetterStatus.FAILED);
        assertThat(letter.getFailureReason()).isEqualTo("SMTP connection timeout");
    }

    @Test
    public void shouldMaterilizeScheduleInfo() {

        Letter letter = Letter.pending(OWNER_ID, CONTENT, createSchedule());
        letter.schedule();

        letter.materializeSendAt(10);

        assertThat(letter.getScheduleInfo().isMaterialized()).isTrue();
        assertThat(letter.getScheduleInfo().sendAt()).isNotNull();
    }

    @Test
    public void shouldBeEqualWhenSameId() {
        LetterId id = LetterId.generate();
        Letter letter1 = Letter.reconstitute(
                id, OWNER_ID, CONTENT, createSchedule(),
                LetterStatus.DRAFT, null, null, null, null);
        Letter letter2 = Letter.reconstitute(
                id, UserId.generate(), CONTENT, createSchedule(),
                LetterStatus.SENT, null, null, null, null);

        assertThat(letter1).isEqualTo(letter2);
    }
}
