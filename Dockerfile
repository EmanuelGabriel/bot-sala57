## Etata do Build
FROM maven:3.8.6-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


## Etapa de execução
FROM openjdk:17-jdk-slim
# Instalação do OpenJDK 17
WORKDIR /app
COPY --from=build /app/target/botsala57-1.0-SNAPSHOT.jar botsala57-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "botsala57-1.0-SNAPSHOT.jar"]




