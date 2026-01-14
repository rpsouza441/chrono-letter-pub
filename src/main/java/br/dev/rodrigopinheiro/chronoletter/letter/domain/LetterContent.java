package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.exception.InvalidLetterContentException;
import br.dev.rodrigopinheiro.chronoletter.shared.domain.ValueObject;

public record LetterContent(String value) implements ValueObject {

    public static final int MIN_LENGTH = 10;
    public static final int MAX_LENGTH = 50_000;

    public LetterContent {
        value = normalize(value);
        validate(value);
    }

    public static LetterContent of(String value) {
        return new LetterContent(value);
    }

    public int characterCount() {
        return value.length();
    }

    private String normalize(String content) {
        if (content == null) {
            throw new InvalidLetterContentException("Letter content cannot be null");
        }
        if (content.isBlank()) {
            throw new InvalidLetterContentException("Letter content cannot be empty or blank");
        }
        return content.trim();
    }

    private void validate(String content) {
        if (content.length() < MIN_LENGTH) {
            throw new InvalidLetterContentException(
                    "Letter content must be at least " + MIN_LENGTH + " characters long");
        }
        if (content.length() > MAX_LENGTH) {
            throw new InvalidLetterContentException(
                    "Letter content must be at most " + MAX_LENGTH + " characters long");
        }
    }

}
