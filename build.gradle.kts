plugins {
    id("dev.slne.surf.api.gradle.standalone")
}

group = "dev.slne.surf.discord"
version = findProperty("version") as String

repositories {
    mavenCentral()
    maven("https://reposilite.slne.dev/releases")
}

surfStandaloneApi {
    withSurfDatabaseR2dbc("2.3.2", "dev.slne.surf.discord.libs")
}

dependencies {
    implementation("io.ktor:ktor-client-cio:3.5.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.5.0")
    implementation("net.dv8tion:JDA:6.4.1")
    implementation("club.minnced:jda-ktx:0.14.2")
    implementation("dev.slne.surf.redis:surf-redis-standalone:1.10.1")
    implementation("com.squareup.okio:okio:3.18.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "dev.slne.surf.discord.DiscordBootstrap"
        attributes["Implementation-Version"] = project.version
    }
}

val dbLibs = "dev.slne.surf.discord.libs.libs"

tasks.shadowJar {
    isZip64 = true

    mergeServiceFiles()

    relocate("io.r2dbc", "$dbLibs.io.r2dbc")
    relocate("org.jetbrains.exposed", "$dbLibs.org.jetbrains.exposed")
    relocate("com.ongres", "$dbLibs.com.ongres")
    relocate("org.mariadb", "$dbLibs.org.mariadb")
}
