package dev.slne.surf.discord

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component

@Component
class DiscordBootstrap {
    @PostConstruct
    fun onLoad() {
        logger.info("Loading Discord Bot...")
    }

    @PreDestroy
    fun onDisable() {
        logger.info("Stopping Discord Bot...")
    }
}