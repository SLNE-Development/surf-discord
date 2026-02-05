package dev.slne.surf.discord

import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import kotlin.properties.Delegates

var dataContext: ConfigurableApplicationContext by Delegates.notNull()

inline fun <reified B : Any> getBean(): B = dataContext.getBean<B>()

fun main(args: Array<String>) {
    SpringApplicationBuilder(DiscordSpringApplication::class.java)
        .profiles("production")
        .initializers(ApplicationContextInitializer<ConfigurableApplicationContext> { applicationContext ->
            dataContext = applicationContext
        })
        .run(*args)
}

@SpringBootApplication(exclude = [R2dbcAutoConfiguration::class])
@EnableCaching
class DiscordSpringApplication

val logger = ComponentLogger.logger("surf-discord")