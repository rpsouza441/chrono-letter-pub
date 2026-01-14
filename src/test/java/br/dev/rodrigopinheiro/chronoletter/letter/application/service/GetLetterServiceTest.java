package br.dev.rodrigopinheiro.chronoletter.letter.application.service;

import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.LetterNotFoundException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.UnauthorizedLetterAccessException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterContent;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterId;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.ScheduleInfo;
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
public class GetLetterServiceTest {

    @Mock
    private LetterRepository letterRepository;

    private GetLetterService getLetterService;

    private UserId ownerId;
    private Letter testLetter;

    @BeforeEach
    public void setUp() {
        getLetterService = new GetLetterService(letterRepository);

        ownerId = UserId.generate();
        testLetter = Letter.draft(
                ownerId,
                LetterContent.of("Conteúdo da carta para teste"),
                ScheduleInfo.of(LocalDate.now().plusDays(30), Timezone.ofDefaultTimezone()));
    }

    @Test
    public void shouldReturnLetterWhenOwnerRequests() {
        // Given
        when(letterRepository.findById(testLetter.getId()))
                .thenReturn(Optional.of(testLetter));

        // When
        var response = getLetterService.execute(
                testLetter.getId().toString(),
                ownerId.toString());

        // Then
        assertThat(response.id()).isEqualTo(testLetter.getId().toString());
        assertThat(response.content()).isEqualTo("Conteúdo da carta para teste");
    }

    @Test
    void shouldThrowWhenLetterNotFound() {
        // Given
        var nonExistentId = LetterId.generate();
        when(letterRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> getLetterService.execute(
                nonExistentId.toString(),
                ownerId.toString())).isInstanceOf(LetterNotFoundException.class);
    }

    @Test
    void shouldThrowWhenNotOwner() {
        // Given
        var anotherUserId = UserId.generate();
        when(letterRepository.findById(testLetter.getId()))
                .thenReturn(Optional.of(testLetter));

        // When/Then
        assertThatThrownBy(() -> getLetterService.execute(
                testLetter.getId().toString(),
                anotherUserId.toString())).isInstanceOf(UnauthorizedLetterAccessException.class);
    }

}
