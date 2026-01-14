package br.dev.rodrigopinheiro.chronoletter.letter.application.exception;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterId;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

public class UnauthorizedLetterAccessException extends RuntimeException {
    public UnauthorizedLetterAccessException(LetterId letterId, UserId userId) {
        super("User: " + userId + " is not authorized to access letter: " + letterId);
    }
}
