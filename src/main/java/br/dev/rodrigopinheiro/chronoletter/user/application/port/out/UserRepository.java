package br.dev.rodrigopinheiro.chronoletter.user.application.port.out;

import java.util.Optional;

import br.dev.rodrigopinheiro.chronoletter.user.domain.Email;
import br.dev.rodrigopinheiro.chronoletter.user.domain.User;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

public interface UserRepository {

    void save(User user);
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
    void delete(User user);

}
