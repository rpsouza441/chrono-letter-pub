# ========================================
# Chrono Letter - Dockerfile
# ========================================
# Multi-stage build para imagem otimizada

# ===== Stage 1: Build =====
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copiar Maven wrapper e pom.xml primeiro (cache de dependências)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Tornar mvnw executável e baixar dependências
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# Copiar código fonte
COPY src/ src/

# Compilar aplicação (sem testes - já rodaram no CI)
RUN ./mvnw package -DskipTests -B && \
    # Renomear JAR para nome fixo
    mv target/*.jar target/app.jar

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar JAR do stage de build
COPY --from=builder /app/target/app.jar app.jar

# Mudar para usuário não-root
USER spring:spring

# Configurações da JVM otimizadas para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -Djava.security.egd=file:/dev/./urandom"

# Porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
