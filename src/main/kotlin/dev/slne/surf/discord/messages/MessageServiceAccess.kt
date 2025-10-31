package dev.slne.surf.discord.messages

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

private lateinit var globalMessageService: MessageService

@Service
class MessageServiceAccess(private val messageService: MessageService) {
    @PostConstruct
    fun init() {
        globalMessageService = messageService
    }
}

fun translatable(key: String, vararg args: String): String =
    globalMessageService.translatable(key, *args)
