import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    `maven-publish`

    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("jvm") version "1.9.25"
    kotlin("plugin.serialization") version "2.1.0"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
}

tasks.withType<BootJar> {
    archiveFileName.set("bot.jar")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.0.1")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-logger-slf4j:4.17.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("it.unimi.dsi:fastutil:8.5.13")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("net.dv8tion:JDA:5.1.2") {
        exclude(module = "opus-java")
    }
    implementation("club.minnced:jda-ktx:0.12.0")
    implementation("com.charleskorn.kaml:kaml-jvm:0.67.0")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.mariadb.jdbc:mariadb-java-client")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

group = "dev.slne"
version = findProperty("version") as String
description = "surf-discord"

kotlin {
    jvmToolchain(21)
}

publishing {
    repositories {
        maven("https://repo.slne.dev/repository/maven-snapshots/") { name = "maven-snapshots" }
    }

    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

//application {
//    mainClass = "dev.slne.discord.DiscordSpringApplicationKt"
//}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-parameters")
    }
}