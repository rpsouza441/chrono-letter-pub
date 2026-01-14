package br.dev.rodrigopinheiro.chronoletter.letter.application.service;

import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.LetterResponse;
import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.UpdateLetterCommand;
import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.LetterNotFoundException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.exception.UnauthorizedLetterAccessException;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.in.UpdateLetterUseCase;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterContent;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterId;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.ScheduleInfo;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

public class UpdateLetterService implements UpdateLetterUseCase {

    private final LetterRepository letterRepository;

    public UpdateLetterService(LetterRepository letterRepository) {
        this.letterRepository = letterRepository;
    }

    @Override
    public LetterResponse execute(UpdateLetterCommand command) {
        Letter letter = findAndAuthorize(command.letterId(), command.requesterId());

        if (command.content() != null) {
            letter.updateContent(LetterContent.of(command.content()));
        }

        if (command.scheduledDate() != null) {
            ScheduleInfo newSchedule = ScheduleInfo.of(
                    command.scheduledDate(),
                    letter.getScheduleInfo().timezone());
            letter.updateSchedule(newSchedule);
        }

        letterRepository.save(letter);
        return LetterResponse.from(letter);
    }

    @Override
    public LetterResponse schedule(String letterId, String requesterId) {
        Letter letter = findAndAuthorize(letterId, requesterId);

        letter.schedule();

        letterRepository.save(letter);
        return LetterResponse.from(letter);
    }

    private Letter findAndAuthorize(String letterId, String requesterId) {
        LetterId id = LetterId.of(letterId);
        UserId userId = UserId.of(requesterId);

        Letter letter = letterRepository.findById(id)
                .orElseThrow(() -> new LetterNotFoundException(id));

        if (!letter.getOwnerId().equals(userId)) {
            throw new UnauthorizedLetterAccessException(id, userId);
        }

        return letter;
    }
}
