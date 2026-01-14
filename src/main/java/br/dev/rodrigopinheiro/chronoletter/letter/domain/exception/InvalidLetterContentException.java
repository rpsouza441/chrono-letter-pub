package br.dev.rodrigopinheiro.chronoletter.letter.domain.exception;

public class InvalidLetterContentException extends RuntimeException {

    public InvalidLetterContentException(String message) {
        super(message);
    }

}
