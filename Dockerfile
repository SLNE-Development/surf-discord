# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN chmod +x gradlew

COPY src src
RUN ./gradlew bootJar --no-daemon \
    && cp "$(find build/libs -maxdepth 1 -type f -name '*.jar' | head -n 1)" /workspace/app.jar

FROM eclipse-temurin:25-jre-alpine

RUN addgroup -S app \
    && adduser -S app -G app \
    && mkdir -p /app/log \
    && chown app:app /app/log
WORKDIR /app

COPY --from=builder --chown=app:app /workspace/app.jar /app/app.jar
COPY --chown=app:app emojis /app/emojis
COPY --chown=app:app gifs /app/gifs

USER app
STOPSIGNAL SIGTERM

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
