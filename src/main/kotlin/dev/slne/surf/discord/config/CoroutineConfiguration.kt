package dev.slne.surf.discord.config

import dev.slne.surf.discord.logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineConfiguration {

    @Bean
    fun discordScope() =
        CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { context, throwable ->
            logger.error(
                "Uncaught exception in coroutine. Context: $context",
                throwable
            )
        })
}