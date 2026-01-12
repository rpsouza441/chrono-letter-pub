package br.dev.rodrigopinheiro.chronoletter.user.domain.exception;

public class InvalidEmailException extends RuntimeException{

    public InvalidEmailException(String email) {
        super("Invalid email: " + (email == null ? "null" : email));
    }

    public InvalidEmailException(String email, Throwable cause) {
        super("Invalid email: " + (email == null ? "null" : email), cause);
    }

}
