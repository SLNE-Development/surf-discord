package dev.slne.surf.discord

import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.ConfigurableApplicationContext

lateinit var dataContext: ConfigurableApplicationContext

inline fun <reified B : Any> getBean(): B = dataContext.getBean<B>()

fun main(args: Array<String>) {
    dataContext = SpringApplicationBuilder(DiscordSpringApplication::class.java)
        .profiles("production")
        .run(*args)
}

@SpringBootApplication
@EnableCaching
@EntityScan
class DiscordSpringApplication

val logger = ComponentLogger.logger()