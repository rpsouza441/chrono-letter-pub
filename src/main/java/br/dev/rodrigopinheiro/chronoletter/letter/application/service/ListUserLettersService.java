package br.dev.rodrigopinheiro.chronoletter.letter.application.service;

import java.util.List;

import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.LetterSummaryResponse;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.in.ListUserLettersUseCase;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

public class ListUserLettersService implements ListUserLettersUseCase {

    private final LetterRepository letterRepository;

    public ListUserLettersService(LetterRepository letterRepository) {
        this.letterRepository = letterRepository;
    }

    @Override
    public List<LetterSummaryResponse> execute(String userId) {
        UserId ownerId = UserId.of(userId);

        return letterRepository.findAllByOwnerId(ownerId).stream()
                .map(LetterSummaryResponse::from)
                .toList();
    }

    @Override
    public List<LetterSummaryResponse> executeByStatus(String userId, LetterStatus status) {
        UserId ownerId = UserId.of(userId);

        return letterRepository.findByOwnerIdAndStatus(ownerId, status).stream()
                .map(LetterSummaryResponse::from)
                .toList();
    }

}
