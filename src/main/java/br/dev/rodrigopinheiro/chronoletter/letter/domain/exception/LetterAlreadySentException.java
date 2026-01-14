package br.dev.rodrigopinheiro.chronoletter.letter.domain.exception;

public class LetterAlreadySentException extends RuntimeException {

    public LetterAlreadySentException() {
        super("Cannot modify a letter that has already been sent or failed");
    }
}
