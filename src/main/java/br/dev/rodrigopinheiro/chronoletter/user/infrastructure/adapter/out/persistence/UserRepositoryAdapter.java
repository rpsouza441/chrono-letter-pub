package br.dev.rodrigopinheiro.chronoletter.user.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.chronoletter.user.application.port.out.UserRepository;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Email;
import br.dev.rodrigopinheiro.chronoletter.user.domain.User;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    
    private final UserJpaRepository jpaRepository;
    
    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public void save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        jpaRepository.save(entity);
    }
    
    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
            .map(UserMapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
            .map(UserMapper::toDomain);
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }
    
    @Override
    public void delete(User user) {
        jpaRepository.deleteById(user.getId().value());
    }
}
