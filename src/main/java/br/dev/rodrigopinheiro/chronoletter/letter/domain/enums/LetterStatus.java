package br.dev.rodrigopinheiro.chronoletter.letter.domain.enums;

public enum LetterStatus {
    DRAFT, // Rascunho (ainda não agendado)
    PENDING, // Agendado, aguardando envio
    SENDING, // Em processo de envio
    SENT, // Enviado com sucesso
    FAILED; // Falha no envio (após todas as tentativas)

    public boolean canEdit() {
        return this == DRAFT || this == PENDING;
    }

    public boolean isFinal() {
        return this == SENT || this == FAILED;
    }
}
