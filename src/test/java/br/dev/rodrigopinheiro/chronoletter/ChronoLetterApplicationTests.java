package br.dev.rodrigopinheiro.chronoletter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class ChronoLetterApplicationTests {

    @Test
    void contextLoads() {
        // Verifica se o contexto Spring carrega sem erros
    }

}
