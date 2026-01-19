package br.dev.rodrigopinheiro.chronoletter.letter.infrastructure.adapter.out.persistence;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.*;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Timezone;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(LetterRepositoryAdapter.class)
public class LetterRepositoryAdapterTest {

    @Autowired
    private LetterRepositoryAdapter letterRepositoryAdapter;

    @Autowired
    private LetterJpaRepository letterJpaRepository;

    private UserId ownerId;

    @BeforeEach
    void setUp() {
        letterJpaRepository.deleteAll();
        ownerId = UserId.generate();
    }

    @Test
    void shouldSaveAndFindLetter() {
        // Given
        Letter letter = createTestLetter();

        // When
        letterRepositoryAdapter.save(letter);
        var found = letterRepositoryAdapter.findById(letter.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(letter.getId());
        assertThat(found.get().getContent().value()).isEqualTo(letter.getContent().value());
    }

    @Test
    void shouldFindAllByOwnerId() {
        // Given
        Letter letter1 = createTestLetter();
        Letter letter2 = createTestLetter();
        letterRepositoryAdapter.save(letter1);
        letterRepositoryAdapter.save(letter2);

        // When
        var letters = letterRepositoryAdapter.findAllByOwnerId(ownerId);

        // Then
        assertThat(letters).hasSize(2);
    }

    @Test
    void shouldDeleteLetter() {
        // Given
        Letter letter = createTestLetter();
        letterRepositoryAdapter.save(letter);

        // When
        letterRepositoryAdapter.delete(letter);

        // Then
        assertThat(letterRepositoryAdapter.findById(letter.getId())).isEmpty();
    }

    private Letter createTestLetter() {
        return Letter.draft(
                ownerId,
                LetterContent.of("Conteúdo da carta de teste"),
                ScheduleInfo.of(LocalDate.now().plusDays(30), Timezone.ofDefaultTimezone()));
    }

}
