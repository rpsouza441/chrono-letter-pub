package br.dev.rodrigopinheiro.chronoletter.user.domain;

import java.util.regex.Pattern;

import br.dev.rodrigopinheiro.chronoletter.shared.domain.ValueObject;
import br.dev.rodrigopinheiro.chronoletter.user.domain.exception.InvalidEmailException;

public record Email(String value) implements ValueObject {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public Email {
        value = normalize(value);
        if (!isValid(value)) {
            throw new InvalidEmailException("Invalid email: " + value);
        }
    }

    public static Email of(String email) {
        return new Email(email);
    }

    public String localPart() {
        return normalize(value.substring(0, value.indexOf("@")));
    }

    public String domain() {
        return normalize(value.substring(value.indexOf("@") + 1));
    }

    private String normalize(String email) {
        if (email == null) {
            throw new InvalidEmailException(null);
        }

        return email.trim().toLowerCase();
    }

    private boolean isValid(String email) {
        return email != null && !email.isBlank() && EMAIL_PATTERN.matcher(email).matches();
    }
}
