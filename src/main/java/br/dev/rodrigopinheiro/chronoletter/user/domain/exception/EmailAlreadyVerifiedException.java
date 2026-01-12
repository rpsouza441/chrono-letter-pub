package br.dev.rodrigopinheiro.chronoletter.user.domain.exception;

public class EmailAlreadyVerifiedException extends RuntimeException {
    public EmailAlreadyVerifiedException() {
        super("Email already verified");
    }
}
