package dev.slne.discord

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

private var _dataContext: ConfigurableApplicationContext? = null
val dataContext: ConfigurableApplicationContext
    get() = _dataContext ?: error("Data context not initialized")

inline fun <reified B : Any> getBean(): B = dataContext.getBean(B::class.java)

fun main(args: Array<String>) {
    _dataContext = runApplication<DiscordSpringApplication>(*args)
}

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories
@EnableJpaAuditing
@EnableTransactionManagement
@EnableFeignClients
class DiscordSpringApplication