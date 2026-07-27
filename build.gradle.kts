import dev.slne.surf.api.gradle.util.slneReleases

plugins {
    id("dev.slne.surf.api.gradle.standalone")
    id("dev.slne.surf.microservice")
}

group = "dev.slne.surf.discord"
version = findProperty("version") as String

repositories {
    mavenCentral()
    slneReleases()
}

surfStandaloneApi {
    withSurfDatabaseR2dbc("2.3.2", "dev.slne.surf.discord.libs")
}

surfMicroservice {
    withMicroserviceApi()
}

dependencies {
//    implementation("io.ktor:ktor-client-cio:3.5.0")
//    implementation("io.ktor:ktor-client-content-negotiation:3.5.0")
    implementation("net.dv8tion:JDA:6.4.1")
    implementation("club.minnced:jda-ktx:0.14.2")
    implementation("dev.slne.surf.redis:surf-redis-standalone:+")
    implementation("com.squareup.okio:okio:3.18.0")
    implementation("ch.qos.logback:logback-classic:1.5.38")
}

tasks.jar {
    manifest {
        attributes["Implementation-Version"] = project.version
    }
}

tasks.shadowJar {
    isZip64 = true
    mergeServiceFiles()
}
