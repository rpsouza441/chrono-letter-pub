package br.dev.rodrigopinheiro.chronoletter.letter.domain.exception;

public class InvalidScheduleDateException extends RuntimeException {

    public InvalidScheduleDateException(String message) {
        super(message);
    }
}
