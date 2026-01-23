package br.dev.rodrigopinheiro.chronoletter.user.infrastructure.adapter.out.persistence;

import br.dev.rodrigopinheiro.chronoletter.user.domain.Email;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Timezone;
import br.dev.rodrigopinheiro.chronoletter.user.domain.User;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;
import br.dev.rodrigopinheiro.chronoletter.user.domain.enums.AuthMethod;

public class UserMapper {
    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().value());
        entity.setEmail(user.getEmail().value());
        entity.setEmailVerified(user.isEmailVerified());
        entity.setTimezone(user.getTimezone().value());
        entity.setAuthMethod(toEntityAuthMethod(user.getAuthMethod()));
        entity.setOauthProvider(user.getOauthProvider());
        entity.setOauthSubject(user.getOauthSubject());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                entity.isEmailVerified(),
                Timezone.of(entity.getTimezone()),
                toDomainAuthMethod(entity.getAuthMethod()),
                entity.getOauthProvider(),
                entity.getOauthSubject(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static AuthMethodEntity toEntityAuthMethod(AuthMethod authMethod) {
        if (authMethod == null)
            return null;
        return AuthMethodEntity.valueOf(authMethod.name());
    }

    private static AuthMethod toDomainAuthMethod(AuthMethodEntity authMethod) {
        if (authMethod == null)
            return null;
        return AuthMethod.valueOf(authMethod.name());
    }
}
