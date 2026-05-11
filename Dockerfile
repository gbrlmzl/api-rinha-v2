# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Copia o wrapper e o pom primeiro pra aproveitar cache de dependencias
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

# Agora copia o codigo e compila
COPY src ./src
RUN ./mvnw -B -ntp clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app

# Usuario nao-root
RUN groupadd -r spring && useradd -r -u 1001 -g spring spring

# Diretorio de logs (montado como volume no compose em prod)
RUN mkdir -p /var/logs/rinhaufpb && chown -R spring:spring /var/logs/rinhaufpb

COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

USER spring
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
