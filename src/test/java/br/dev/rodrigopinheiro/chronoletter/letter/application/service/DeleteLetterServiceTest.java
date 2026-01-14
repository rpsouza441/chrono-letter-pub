package br.dev.rodrigopinheiro.chronoletter.letter.application.service;

import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.LetterNotFoundException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.UnauthorizedLetterAccessException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterContent;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterId;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.ScheduleInfo;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Timezone;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteLetterServiceTest {

    @Mock
    private LetterRepository letterRepository;

    private DeleteLetterService deleteLetterService;

    private UserId ownerId;
    private Letter testLetter;

    @BeforeEach
    void setUp() {
        deleteLetterService = new DeleteLetterService(letterRepository);

        ownerId = UserId.generate();
        testLetter = Letter.draft(
                ownerId,
                LetterContent.of("Carta para ser deletada"),
                ScheduleInfo.of(LocalDate.now().plusDays(30), Timezone.ofDefaultTimezone()));
    }

    @Test
    void shouldDeleteLetter() {
        // Given
        when(letterRepository.findById(testLetter.getId()))
                .thenReturn(Optional.of(testLetter));

        // When
        deleteLetterService.execute(
                testLetter.getId().toString(),
                ownerId.toString());

        // Then
        verify(letterRepository).delete(testLetter);
    }

    @Test
    void shouldThrowWhenLetterNotFound() {
        // Given
        var nonExistentId = LetterId.generate();
        when(letterRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> deleteLetterService.execute(
                nonExistentId.toString(),
                ownerId.toString())).isInstanceOf(LetterNotFoundException.class);
    }

    @Test
    void shouldThrowWhenNotOwner() {
        // Given
        var anotherUser = UserId.generate();
        when(letterRepository.findById(testLetter.getId()))
                .thenReturn(Optional.of(testLetter));

        // When/Then
        assertThatThrownBy(() -> deleteLetterService.execute(
                testLetter.getId().toString(),
                anotherUser.toString())).isInstanceOf(UnauthorizedLetterAccessException.class);

        verify(letterRepository, never()).delete(any());
    }

    @Test
    void shouldAllowDeletingSentLetter() {
        // Given - carta já enviada
        var sentLetter = Letter.reconstitute(
                LetterId.generate(),
                ownerId,
                LetterContent.of("Carta já enviada no passado"),
                ScheduleInfo.of(LocalDate.now().plusDays(30), Timezone.ofDefaultTimezone()),
                LetterStatus.SENT,
                Instant.now(),
                null,
                Instant.now(),
                Instant.now());

        when(letterRepository.findById(sentLetter.getId()))
                .thenReturn(Optional.of(sentLetter));

        // When
        deleteLetterService.execute(
                sentLetter.getId().toString(),
                ownerId.toString());

        // Then - deve permitir deletar mesmo enviada
        verify(letterRepository).delete(sentLetter);
    }
}
