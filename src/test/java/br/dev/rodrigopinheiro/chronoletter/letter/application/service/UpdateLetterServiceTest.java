package br.dev.rodrigopinheiro.chronoletter.letter.application.service;

import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.UpdateLetterCommand;
import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.LetterNotFoundException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.UnauthorizedLetterAccessException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterContent;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.ScheduleInfo;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Timezone;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLetterServiceTest {

    @Mock
    private LetterRepository letterRepository;

    private UpdateLetterService updateLetterService;

    private UserId ownerId;
    private Letter testLetter;

    @BeforeEach
    void setUp() {
        updateLetterService = new UpdateLetterService(letterRepository);

        ownerId = UserId.generate();
        testLetter = Letter.draft(
                ownerId,
                LetterContent.of("Conteúdo original da carta"),
                ScheduleInfo.of(LocalDate.now().plusDays(30), Timezone.ofDefaultTimezone()));
    }

    @Test
    void shouldUpdateContent() {
        // Given
        when(letterRepository.findById(testLetter.getId()))
                .thenReturn(Optional.of(testLetter));

        var command = new UpdateLetterCommand(
                testLetter.getId().toString(),
                ownerId.toString(),
                "Novo conteúdo atualizado da carta",
                null); // não atualiza data

        // When
        var response = updateLetterService.execute(command);

        // Then
        assertThat(response.content()).isEqualTo("Novo conteúdo atualizado da carta");
        verify(letterRepository).save(testLetter);
    }

    @Test
    void shouldUpdateScheduleDate() {
        // Given
        when(letterRepository.findById(testLetter.getId()))
                .thenReturn(Optional.of(testLetter));

        var newDate = LocalDate.now().plusDays(60);
        var command = new UpdateLetterCommand(
                testLetter.getId().toString(),
                ownerId.toString(),
                null, // não atualiza conteúdo
                newDate);

        // When
        var response = updateLetterService.execute(command);

        // Then
        assertThat(response.scheduledDate()).isEqualTo(newDate);
        verify(letterRepository).save(testLetter);
    }

    @Test
    void shouldScheduleLetter() {
        // Given
        when(letterRepository.findById(testLetter.getId()))
                .thenReturn(Optional.of(testLetter));

        // When
        var response = updateLetterService.schedule(
                testLetter.getId().toString(),
                ownerId.toString());

        // Then
        assertThat(response.status()).isEqualTo(LetterStatus.PENDING.name());
        verify(letterRepository).save(testLetter);
    }

    @Test
    void shouldThrowWhenNotOwner() {
        // Given
        var anotherUser = UserId.generate();
        when(letterRepository.findById(testLetter.getId()))
                .thenReturn(Optional.of(testLetter));

        var command = new UpdateLetterCommand(
                testLetter.getId().toString(),
                anotherUser.toString(),
                "Tentando atualizar carta de outro usuário",
                null);

        // When/Then
        assertThatThrownBy(() -> updateLetterService.execute(command))
                .isInstanceOf(UnauthorizedLetterAccessException.class);
    }

    @Test
    void shouldThrowWhenLetterNotFound() {
        // Given
        when(letterRepository.findById(any()))
                .thenReturn(Optional.empty());

        var command = new UpdateLetterCommand(
                testLetter.getId().toString(),
                ownerId.toString(),
                "Conteúdo qualquer",
                null);

        // When/Then
        assertThatThrownBy(() -> updateLetterService.execute(command))
                .isInstanceOf(LetterNotFoundException.class);
    }
}
