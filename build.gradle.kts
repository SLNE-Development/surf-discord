plugins {
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("dev.slne.surf.api.gradle.core")
}

group = "dev.slne.surf.discord"
version = findProperty("version") as String

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("io.ktor:ktor-client-cio:3.5.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.5.0")
    implementation("com.charleskorn.kaml:kaml-jvm:0.104.0")
    implementation("net.dv8tion:JDA:6.4.1")
    implementation("club.minnced:jda-ktx:0.14.2")

    implementation("dev.slne.surf:surf-database-r2dbc:2.3.1")
}

kotlin { jvmToolchain(25) }