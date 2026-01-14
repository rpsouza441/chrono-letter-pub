package br.dev.rodrigopinheiro.chronoletter.letter.application.port.in;

public interface DeleteLetterUseCase {

    void execute(String letterId, String requesterId);
}
