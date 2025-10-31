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
    implementation("org.jetbrains.exposed:exposed-jdbc:0.55.0")
    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("it.unimi.dsi:fastutil:8.5.16")
    implementation("net.kyori:adventure-api:4.24.0")
    implementation("net.kyori:adventure-text-logger-slf4j:4.24.0")
    implementation("com.charleskorn.kaml:kaml-jvm:0.72.0")
    implementation("net.dv8tion:JDA:5.6.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.2.0")
}

kotlin { jvmToolchain(21) }
