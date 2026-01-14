package br.dev.rodrigopinheiro.chronoletter.letter.application.dto;

import java.time.LocalDate;

public record UpdateLetterCommand(
                String letterId,
                String requesterId,
                String content,
                LocalDate scheduledDate) {

}
