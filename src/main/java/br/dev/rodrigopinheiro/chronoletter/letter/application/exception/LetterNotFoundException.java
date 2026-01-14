package br.dev.rodrigopinheiro.chronoletter.letter.application.exception;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterId;

public class LetterNotFoundException extends RuntimeException {
    public LetterNotFoundException(LetterId id) {
        super("Letter not found: " + id);
    }
}
