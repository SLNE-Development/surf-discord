package dev.slne.discord

import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

lateinit var dataContext: ConfigurableApplicationContext

inline fun <reified B : Any> getBean(): B = dataContext.getBean<B>()

fun main(args: Array<String>) {
    dataContext = SpringApplicationBuilder(DiscordSpringApplication::class.java)
        .profiles("production")
        .run(*args)
}

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@EnableCaching
@EnableJpaRepositories
@EnableJpaAuditing
@EnableTransactionManagement
@EnableFeignClients
@EntityScan
class DiscordSpringApplication