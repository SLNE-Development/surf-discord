plugins {
    java
    `maven-publish`

    id("org.hibernate.build.maven-repo-auth") version "3.0.4"
    id("com.gradleup.shadow") version "8.3.3"

    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.21"
    kotlin("plugin.noarg") version "2.0.21"

    application
}

repositories {
    gradlePluginPortal()
    mavenCentral()

    maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    maven("https://repo.slne.dev/repository/maven-snapshots/") { name = "maven-snapshots" }
    maven("https://repo.slne.dev/repository/maven-proxy/") { name = "maven-proxy" }
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, "seconds")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.0.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-logger-slf4j:4.17.0")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("com.squareup.okhttp3:okhttp-coroutines:5.0.0-alpha.14")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.hibernate.orm:hibernate-core:6.6.1.Final")
    implementation("it.unimi.dsi:fastutil:8.5.13")
    implementation("net.dv8tion:JDA:5.1.2") {
        exclude(module = "opus-java")
    }
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-jackson:4.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("club.minnced:jda-ktx:0.12.0")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.4.1")
}

group = "dev.slne"
version = "5.0.0-SNAPSHOT"
description = "surf-discord"

java {
    withJavadocJar()
    withSourcesJar()

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

publishing {
    repositories {
        maven("https://repo.slne.dev/repository/maven-snapshots/") { name = "maven-snapshots" }
    }

    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}


application {
    mainClass.set("dev.slne.discord.BootstrapKt")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-parameters")
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    shadowJar {
        archiveFileName.set("bot.jar")
    }
}

tasks.named<Jar>("jar") {
    archiveFileName.set("bot.jar")
}

tasks.withType<Zip> {
    isZip64 = true
}