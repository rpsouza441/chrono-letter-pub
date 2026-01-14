package br.dev.rodrigopinheiro.chronoletter.letter.application.dto;

import java.time.LocalDate;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;

public record LetterSummaryResponse(
        String id,
        String contentPreview,
        LocalDate scheduledDate,
        String status,
        int characterCount) {

    private static final int PREVIEW_LENGTH = 100;

    public static LetterSummaryResponse from(Letter letter) {
        String content = letter.getContent().value();
        String preview = content.length() > PREVIEW_LENGTH
                ? content.substring(0, PREVIEW_LENGTH) + "..."
                : content;

        return new LetterSummaryResponse(
                letter.getId().toString(),
                preview,
                letter.getScheduleInfo().scheduledDate(),
                letter.getStatus().name(),
                letter.getContent().characterCount());
    }

}
