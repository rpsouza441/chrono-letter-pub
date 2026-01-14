package br.dev.rodrigopinheiro.chronoletter.letter.application.service;

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
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListUserLettersServiceTest {

    @Mock
    private LetterRepository letterRepository;

    private ListUserLettersService listUserLettersService;

    private UserId ownerId;

    @BeforeEach
    void setUp() {
        listUserLettersService = new ListUserLettersService(letterRepository);
        ownerId = UserId.generate();
    }

    @Test
    void shouldReturnAllUserLetters() {
        // Given
        var letter1 = createTestLetter("Primeira carta para o futuro!");
        var letter2 = createTestLetter("Segunda carta com mais conteúdo para testar");

        when(letterRepository.findAllByOwnerId(ownerId))
                .thenReturn(List.of(letter1, letter2));

        // When
        var responses = listUserLettersService.execute(ownerId.toString());

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).contentPreview()).contains("Primeira carta");
    }

    @Test
    void shouldReturnEmptyListWhenNoLetters() {
        // Given
        when(letterRepository.findAllByOwnerId(ownerId))
                .thenReturn(List.of());

        // When
        var responses = listUserLettersService.execute(ownerId.toString());

        // Then
        assertThat(responses).isEmpty();
    }

    @Test
    void shouldFilterByStatus() {
        // Given
        var pendingLetter = createTestLetter("Carta agendada");
        pendingLetter.schedule();

        when(letterRepository.findByOwnerIdAndStatus(ownerId, LetterStatus.PENDING))
                .thenReturn(List.of(pendingLetter));

        // When
        var responses = listUserLettersService.executeByStatus(
                ownerId.toString(),
                LetterStatus.PENDING);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).status()).isEqualTo("PENDING");
    }

    @Test
    void shouldTruncateLongContentInPreview() {
        // Given
        var longContent = "A".repeat(200);
        var letter = createTestLetter(longContent);

        when(letterRepository.findAllByOwnerId(ownerId))
                .thenReturn(List.of(letter));

        // When
        var responses = listUserLettersService.execute(ownerId.toString());

        // Then
        assertThat(responses.get(0).contentPreview()).hasSize(103); // 100 + "..."
        assertThat(responses.get(0).contentPreview()).endsWith("...");
    }

    private Letter createTestLetter(String content) {
        return Letter.draft(
                ownerId,
                LetterContent.of(content),
                ScheduleInfo.of(LocalDate.now().plusDays(30), Timezone.ofDefaultTimezone()));
    }
}