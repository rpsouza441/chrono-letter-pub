package br.dev.rodrigopinheiro.chronoletter.letter.infrastructure.adapter.in.web.dto;

import java.time.LocalDate;

public record CreateLetterRequest(
        String content,
        LocalDate scheduledDate,
        String timezone) {
}