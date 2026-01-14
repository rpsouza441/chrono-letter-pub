package br.dev.rodrigopinheiro.chronoletter.letter.application.service;

import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.LetterNotFoundException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.UnauthorizedLetterAccessException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.in.DeleteLetterUseCase;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterId;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

public class DeleteLetterService implements DeleteLetterUseCase {

    private final LetterRepository letterRepository;

    public DeleteLetterService(LetterRepository letterRepository) {
        this.letterRepository = letterRepository;
    }

    @Override
    public void execute(String letterId, String requesterId) {
        LetterId id = LetterId.of(letterId);
        UserId userId = UserId.of(requesterId);

        Letter letter = letterRepository.findById(id)
                .orElseThrow(() -> new LetterNotFoundException(id));

        if (!letter.getOwnerId().equals(userId)) {
            throw new UnauthorizedLetterAccessException(id, userId);
        }

        letterRepository.delete(letter);
    }
}
