package br.dev.rodrigopinheiro.chronoletter.letter.infrastructure.config;

import br.dev.rodrigopinheiro.chronoletter.letter.application.port.in.*;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.out.LetterRepository;
import br.dev.rodrigopinheiro.chronoletter.letter.application.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LetterModuleConfig {

    @Bean
    public CreateLetterUseCase createLetterUseCase(LetterRepository letterRepository) {
        return new CreateLetterService(letterRepository);
    }

    @Bean
    public GetLetterUseCase getLetterUseCase(LetterRepository letterRepository) {
        return new GetLetterService(letterRepository);
    }

    @Bean
    public ListUserLettersUseCase listUserLettersUseCase(LetterRepository letterRepository) {
        return new ListUserLettersService(letterRepository);
    }

    @Bean
    public UpdateLetterUseCase updateLetterUseCase(LetterRepository letterRepository) {
        return new UpdateLetterService(letterRepository);
    }

    @Bean
    public DeleteLetterUseCase deleteLetterUseCase(LetterRepository letterRepository) {
        return new DeleteLetterService(letterRepository);
    }
}
