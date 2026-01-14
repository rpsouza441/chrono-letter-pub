package br.dev.rodrigopinheiro.chronoletter.letter.application.port.in;

import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.LetterResponse;
import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.UpdateLetterCommand;

public interface UpdateLetterUseCase {

    LetterResponse execute(UpdateLetterCommand command);

    LetterResponse schedule(String letterId, String requesterId);
}
