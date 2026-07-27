# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM eclipse-temurin:25-jdk-jammy AS builder
WORKDIR /workspace

COPY gradlew ./
COPY gradle ./gradle
COPY gradle.properties settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew

# Warm the Gradle/dependency cache before copying sources so code-only changes don't bust it
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon help

COPY src ./src

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon shadowJar --parallel --no-scan \
    && cp build/libs/*.jar /workspace/app.jar

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre-jammy AS runtime

RUN useradd --system --create-home --shell /usr/sbin/nologin bot
WORKDIR /app

COPY --from=builder /workspace/app.jar ./app.jar

RUN mkdir -p /app/log && chown -R bot:bot /app

USER bot

# Discord bot
ENV BOT_TOKEN=""

# Channels
ENV TICKET_CHANNEL=""
ENV TICKET_LOG_CHANNEL=""

# Database (surf-database)
ENV SURF_DATABASE_TYPE="POSTGRESQL"
ENV SURF_DATABASE_HOST=""
ENV SURF_DATABASE_PORT="5432"
ENV SURF_DATABASE_SCHEMA="public"
ENV SURF_DATABASE_NAME=""
ENV SURF_DATABASE_USERNAME=""
ENV SURF_DATABASE_PASSWORD=""

# Roles
ENV PREMIUM_ROLE_ID=""

# LuckPerms API (optional)
ENV LUCKPERMS_URL="http://localhost:8080"
ENV LUCKPERMS_TOKEN=""

ENTRYPOINT ["java", "-jar", "app.jar"]