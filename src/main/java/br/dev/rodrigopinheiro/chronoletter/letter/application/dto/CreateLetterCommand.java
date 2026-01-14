package br.dev.rodrigopinheiro.chronoletter.letter.application.dto;

import java.time.LocalDate;

public record CreateLetterCommand(
        String ownerId,
        String content,
        LocalDate scheduledDate,
        String timezoneString) {

}
