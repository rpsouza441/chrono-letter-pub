package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.exception.InvalidLetterContentException;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class LetterContentTest {

    @Test
    public void shouldCreateValidContent() {
        LetterContent letterContent = LetterContent.of("Querido eu do futuro, espero que esteja bem!");

        assertThat(letterContent).isNotNull();
        assertThat(letterContent.value()).isEqualTo("Querido eu do futuro, espero que esteja bem!");
    }

    @Test
    public void shouldTrimWhiteSpaces() {
        LetterContent letterContent = LetterContent.of(" Querido eu do futuro, espero que esteja bem! ");

        assertThat(letterContent).isNotNull();
        assertThat(letterContent.value()).isEqualTo("Querido eu do futuro, espero que esteja bem!");
    }

    @Test
    public void shouldRejectNullContent() {
        assertThatThrownBy(() -> LetterContent.of(null))
                .isInstanceOf(InvalidLetterContentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    public void shouldRejectEmptyContent() {
        assertThatThrownBy(() -> LetterContent.of(""))
                .isInstanceOf(InvalidLetterContentException.class)
                .hasMessageContaining("empty or blank");
    }

    @Test
    public void shouldRejectBlankContent() {
        assertThatThrownBy(() -> LetterContent.of("   "))
                .isInstanceOf(InvalidLetterContentException.class)
                .hasMessageContaining("empty or blank");
    }

    @Test
    public void shouldRejectContentTooShort() {
        assertThatThrownBy(() -> LetterContent.of("Hi!"))
                .isInstanceOf(InvalidLetterContentException.class)
                .hasMessageContaining("at least");
    }

    @Test
    public void shouldRejectContentTooLong() {
        String longContent = "a".repeat(50001); // Max 50000 chars
        assertThatThrownBy(() -> LetterContent.of(longContent))
                .isInstanceOf(InvalidLetterContentException.class)
                .hasMessageContaining("at most");
    }

    @Test
    public void shloudAcceptContentWithValidLength() {
        String validContent = "a".repeat(50000); // Max 50000 chars
        LetterContent letterContent = LetterContent.of(validContent);

        assertThat(letterContent).isNotNull();
        assertThat(letterContent.value()).isEqualTo(validContent);
    }

    @Test
    public void shouldCalculateCharacterCount() {
        LetterContent content = LetterContent.of("Hello World!");

        assertThat(content).isNotNull();
        assertThat(content.characterCount()).isEqualTo(12);
    }

    @Test
    public void shouldBeEqualWhenSameContent() {
        LetterContent content1 = LetterContent.of("Hello World!");
        LetterContent content2 = LetterContent.of("Hello World!");

        assertThat(content1).isEqualTo(content2);
        assertThat(content1).isEqualTo(content2);
    }

}
