package br.dev.rodrigopinheiro.chronoletter.letter.application.port.out;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterId;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

public interface LetterRepository {

    void save(Letter letter);

    Optional<Letter> findById(LetterId id);

    List<Letter> findAllByOwnerId(UserId ownerId);

    List<Letter> findByOwnerIdAndStatus(UserId ownerId, LetterStatus status);

    /**
     * Busca cartas pendentes prontas para materialização.
     * Cartas com scheduled_date <= data limite e send_at IS NULL.
     */
    List<Letter> findPendingForMaterialization(LocalDate scheduledDateLimit);

    /**
     * Busca cartas prontas para envio.
     * Cartas com status PENDING, send_at <= agora.
     */
    List<Letter> findReadyToSend(Instant now, int limit);

    void delete(Letter letter);

    void deleteAllByOwnerId(UserId ownerId);

}
