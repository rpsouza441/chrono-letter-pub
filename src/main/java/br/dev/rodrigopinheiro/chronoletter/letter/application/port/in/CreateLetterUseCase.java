package br.dev.rodrigopinheiro.chronoletter.letter.application.port.in;

import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.CreateLetterCommand;
import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.LetterResponse;

public interface CreateLetterUseCase {

    LetterResponse execute(CreateLetterCommand command);

}
