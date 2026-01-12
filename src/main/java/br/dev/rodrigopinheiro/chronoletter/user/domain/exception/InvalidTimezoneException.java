package br.dev.rodrigopinheiro.chronoletter.user.domain.exception;

public class InvalidTimezoneException extends RuntimeException {

    public InvalidTimezoneException(String timezone) {
        super("Invalid timezone: " + (timezone == null ? "null" : timezone));
    }

    public InvalidTimezoneException(String timezone, Throwable cause) {
        super("Invalid timezone: " + (timezone == null ? "null" : timezone), cause);
    }

}
