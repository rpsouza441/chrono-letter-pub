package br.dev.rodrigopinheiro.chronoletter.letter.application.dto;

import java.time.Instant;
import java.time.LocalDate;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;

public record LetterResponse(
        String id,
        String ownerId,
        String content,
        LocalDate scheduledDate,
        String timezone,
        Instant sendAt,
        String status,
        Instant sentAt,
        String failureReason,
        Instant createdAt,
        Instant updatedAt) {

    public static LetterResponse from(Letter letter) {
        return new LetterResponse(
                letter.getId().toString(),
                letter.getOwnerId().toString(),
                letter.getContent().value(),
                letter.getScheduleInfo().scheduledDate(),
                letter.getScheduleInfo().timezone().value(),
                letter.getScheduleInfo().sendAt(),
                letter.getStatus().name(),
                letter.getSentAt(),
                letter.getFailureReason(),
                letter.getCreatedAt(),
                letter.getUpdatedAt());
    }

}
