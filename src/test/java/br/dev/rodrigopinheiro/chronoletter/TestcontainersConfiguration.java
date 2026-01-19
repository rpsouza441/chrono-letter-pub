package br.dev.rodrigopinheiro.chronoletter;

import org.springframework.boot.test.context.TestConfiguration;
// Commented out: Testcontainers requires Docker
// Uncomment these imports and the bean when Docker is available
// import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
// import org.testcontainers.containers.PostgreSQLContainer;
// import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    // Uncomment when Docker is available for integration tests
    // @Bean
    // @ServiceConnection
    // PostgreSQLContainer<?> postgresContainer() {
    // return new
    // PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));
    // }

}
