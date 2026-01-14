package br.dev.rodrigopinheiro.chronoletter.letter.domain.enums;

public enum DeliveryAttemptStatus {

    PENDING, // Tentativa iniciada
    SUCCESS, // Enviado com sucesso
    FAILED, // Falhou definitivamente
    RETRYING // Falhou mas vai tentar novamente

}
