package br.dev.rodrigopinheiro.chronoletter.letter.application.service;

import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.CreateLetterCommand;
import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.LetterResponse;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateLetterServiceTest {

    @Mock
    private LetterRepository letterRepository;

    private CreateLetterService createLetterService;

    @BeforeEach
    public void setUp() {
        createLetterService = new CreateLetterService(letterRepository);
    }

    @Test
    public void shouldCreateDraftLetter() {
        // Given
        var command = new CreateLetterCommand(
                UUID.randomUUID().toString(),
                "Querido eu do futuro, espero que você esteja bem.",
                LocalDate.now().plusDays(30),
                "America/Sao_Paulo");

        // when
        LetterResponse response = createLetterService.execute(command);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(response.ownerId()).isEqualTo(command.ownerId());
        assertThat(response.content()).isEqualTo(command.content());
        assertThat(response.scheduledDate()).isEqualTo(command.scheduledDate());
        assertThat(response.status()).isEqualTo(LetterStatus.DRAFT.name());

        ArgumentCaptor<Letter> letterCaptor = ArgumentCaptor.forClass(Letter.class);
        verify(letterRepository).save(letterCaptor.capture());

        Letter savedLetter = letterCaptor.getValue();
        assertThat(savedLetter.getId()).isNotNull();
        assertThat(savedLetter.getOwnerId().toString()).isEqualTo(command.ownerId());
        assertThat(savedLetter.getContent().value()).isEqualTo(command.content());
        assertThat(savedLetter.getScheduleInfo().scheduledDate()).isEqualTo(command.scheduledDate());
        assertThat(savedLetter.getScheduleInfo().timezone().value()).isEqualTo(command.timezoneString());
        assertThat(savedLetter.getStatus()).isEqualTo(LetterStatus.DRAFT);

    }

    @Test
    public void shouldRejectInvalidCommand() {
        // Given
        var command = new CreateLetterCommand(
                UUID.randomUUID().toString(),
                "Hi.",
                LocalDate.now().plusDays(30),
                "America/Sao_Paulo");

        // When/Then
        assertThatThrownBy(() -> createLetterService.execute(command))
                .hasMessageContaining("at least");
    }

    @Test
    public void shouldRejectPastScheduleDate() {
        // Given
        var command = new CreateLetterCommand(
                UUID.randomUUID().toString(),
                "Querido eu do futuro, espero que você esteja bem.",
                LocalDate.now().minusDays(1),
                "America/Sao_Paulo");

        // When/Then
        assertThatThrownBy(() -> createLetterService.execute(command))
                .hasMessageContaining("future");
    }

}
