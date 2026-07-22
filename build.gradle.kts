plugins {
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("dev.slne.surf.api.gradle.standalone")
}

group = "dev.slne.surf.discord"
version = findProperty("version") as String

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("io.ktor:ktor-client-cio:3.5.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.5.0")
    implementation("com.charleskorn.kaml:kaml-jvm:0.104.0")
    implementation("net.dv8tion:JDA:6.4.1")
    implementation("club.minnced:jda-ktx:0.14.2")
}

surfStandaloneApi {
    withSurfDatabaseR2dbc("2.3.1", "dev.slne.surf.discord.libs.database")
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlinx" &&
            requested.name.startsWith("kotlinx-coroutines")
        ) {
            useVersion("1.11.0")
        }
    }
}

kotlin { jvmToolchain(25) }