package br.dev.rodrigopinheiro.chronoletter.letter.application.port.in;

import java.util.List;

import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.LetterSummaryResponse;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;

public interface ListUserLettersUseCase {
    List<LetterSummaryResponse> execute(String userId);

    List<LetterSummaryResponse> executeByStatus(String userId, LetterStatus status);

}
