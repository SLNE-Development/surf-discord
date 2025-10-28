package dev.slne.surf.discord.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineConfiguration {
    @Bean
    fun discordScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}