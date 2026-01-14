package br.dev.rodrigopinheiro.chronoletter.letter.application.service;


import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.CreateLetterCommand;
import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.LetterResponse;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.in.CreateLetterUseCase;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterContent;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.ScheduleInfo;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Timezone;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

public class CreateLetterService implements CreateLetterUseCase {

    private final LetterRepository letterRepository;

    public CreateLetterService(LetterRepository letterRepository) {
        this.letterRepository = letterRepository;
    }

    @Override
    public LetterResponse execute(CreateLetterCommand command) {
        Letter letter = createLetter(command);
        letterRepository.save(letter);
        return LetterResponse.from(letter);
    }

    public LetterResponse executeAndSchedule(CreateLetterCommand command) {
        Letter letter = createLetter(command);
        letter.schedule();
        letterRepository.save(letter);
        return LetterResponse.from(letter);
    }

    private Letter createLetter(CreateLetterCommand command) {
        UserId ownerId= UserId.of(command.ownerId());
        LetterContent content = LetterContent.of(command.content());
        Timezone timezone = Timezone.of(command.timezoneString());
        ScheduleInfo scheduleInfo = ScheduleInfo.of(command.scheduledDate(), timezone);
        return Letter.draft(ownerId, content, scheduleInfo);
    }

}
