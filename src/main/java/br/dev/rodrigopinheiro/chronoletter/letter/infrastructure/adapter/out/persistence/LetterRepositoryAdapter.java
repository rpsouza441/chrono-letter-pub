package br.dev.rodrigopinheiro.chronoletter.letter.infrastructure.adapter.out.persistence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterId;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

@Repository
public class LetterRepositoryAdapter implements LetterRepository {

    private final LetterJpaRepository jpaRepository;

    public LetterRepositoryAdapter(LetterJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Letter letter) {
        LetterEntity entity = LetterMapper.toEntity(letter);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Letter> findById(LetterId id) {
        return jpaRepository.findById(id.value())
                .map(LetterMapper::toDomain);
    }

    @Override
    public List<Letter> findAllByOwnerId(UserId ownerId) {
        return jpaRepository.findAllByUserId(ownerId.value()).stream()
                .map(LetterMapper::toDomain)
                .toList();
    }

    @Override
    public List<Letter> findByOwnerIdAndStatus(UserId ownerId, LetterStatus status) {
        LetterStatusEntity statusEntity = LetterStatusEntity.valueOf(status.name());
        return jpaRepository.findByUserIdAndStatus(ownerId.value(), statusEntity).stream()
                .map(LetterMapper::toDomain)
                .toList();
    }

    @Override
    public List<Letter> findPendingForMaterialization(LocalDate scheduledDateLimit) {
        return jpaRepository.findPendingForMaterialization(scheduledDateLimit).stream()
                .map(LetterMapper::toDomain)
                .toList();
    }

    @Override
    public List<Letter> findReadyToSend(Instant now, int limit) {
        return jpaRepository.findReadyToSend(now, PageRequest.of(0, limit)).stream()
                .map(LetterMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Letter letter) {
        jpaRepository.deleteById(letter.getId().value());
    }

    @Override
    public void deleteAllByOwnerId(UserId ownerId) {
        jpaRepository.deleteAllByUserId(ownerId.value());
    }
}
