package br.dev.rodrigopinheiro.chronoletter;

import org.springframework.boot.SpringApplication;

public class TestChronoLetterApplication {

    public static void main(String[] args) {
        SpringApplication.from(ChronoLetterApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}
