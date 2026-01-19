package br.dev.rodrigopinheiro.chronoletter.letter.infrastructure.adapter.in.web.dto;

import java.time.LocalDate;

public record UpdateLetterRequest(
        String content,
        LocalDate scheduledDate) {

}
