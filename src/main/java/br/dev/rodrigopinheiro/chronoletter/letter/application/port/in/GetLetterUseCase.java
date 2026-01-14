package br.dev.rodrigopinheiro.chronoletter.letter.application.port.in;

import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.LetterResponse;

public interface GetLetterUseCase {
    LetterResponse execute(String letterId, String requesterId);
}
