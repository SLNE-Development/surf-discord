plugins {
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
    kotlin("plugin.jpa") version "2.2.10"
    kotlin("plugin.spring") version "2.2.10"
}

group = "dev.slne.surf.discord"
version = findProperty("version") as String

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("io.ktor:ktor-client-core:3.3.2")
    implementation("io.ktor:ktor-client-cio:3.3.2")
    implementation("io.ktor:ktor-client-content-negotiation:3.3.2")
    implementation("org.jetbrains.exposed:exposed-r2dbc:1.0.0")
    implementation("org.jetbrains.exposed:exposed-core:1.0.0")
    implementation("org.jetbrains.exposed:exposed-json:1.0.0")
    implementation("org.jetbrains.exposed:exposed-java-time:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("it.unimi.dsi:fastutil:8.5.16")
    implementation("net.kyori:adventure-api:4.24.0")
    implementation("net.kyori:adventure-text-logger-slf4j:4.24.0")
    implementation("com.charleskorn.kaml:kaml-jvm:0.72.0")
    implementation("net.dv8tion:JDA:6.1.0")
    implementation("club.minnced:jda-ktx:0.12.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")

    runtimeOnly("org.mariadb:r2dbc-mariadb:1.3.0")
}

kotlin { jvmToolchain(21) }