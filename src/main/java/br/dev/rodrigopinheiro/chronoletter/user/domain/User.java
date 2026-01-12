package br.dev.rodrigopinheiro.chronoletter.user.domain;

import java.time.Instant;
import java.util.Objects;

import br.dev.rodrigopinheiro.chronoletter.shared.domain.AggregateRoot;
import br.dev.rodrigopinheiro.chronoletter.user.domain.enums.AuthMethod;
import br.dev.rodrigopinheiro.chronoletter.user.domain.exception.EmailAlreadyVerifiedException;

public class User extends AggregateRoot<UserId> {

    private final Email email;
    private boolean emailVerified;
    private Timezone timezone;
    private AuthMethod authMethod;
    private String oauthProvider;
    private String oauthSubject;

    User(UserId id, Email email, Timezone timezone) {
        super(id);
        this.email = email;
        this.timezone = timezone;
        this.emailVerified = false;

    }

 // Construtor para reconstituição do banco
    private User(
        UserId id,
        Email email,
        boolean emailVerified,
        Timezone timezone,
        AuthMethod authMethod,
        String oauthProvider,
        String oauthSubject,
        Instant createdAt,
        Instant updatedAt
    ) {
        super(id, createdAt, updatedAt);
        this.email = email;
        this.emailVerified = emailVerified;
        this.timezone = timezone;
        this.authMethod = authMethod;
        this.oauthProvider = oauthProvider;
        this.oauthSubject = oauthSubject;
    }

     
    // Factory method para novo usuário
    public static User register(Email email, Timezone timezone) {
        return new User(UserId.generate(), email, timezone);
    }
    
    // Factory method para reconstituição
    public static User reconstitute(
        UserId id,
        Email email,
        boolean emailVerified,
        Timezone timezone,
        AuthMethod authMethod,
        String oauthProvider,
        String oauthSubject,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new User(id, email, emailVerified, timezone, authMethod, oauthProvider, oauthSubject, createdAt, updatedAt);
    }
    
    public void verifyEmail() {
        if (emailVerified) {
            throw new EmailAlreadyVerifiedException();
        }
        this.emailVerified = true;
        touch();
    }
    
    public void changeTimezone(Timezone newTimezone) {
        this.timezone = Objects.requireNonNull(newTimezone);
        touch();
    }

    public Email getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public AuthMethod getAuthMethod() {
        return authMethod;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public String getOauthSubject() {
        return oauthSubject;
    }

    

}
