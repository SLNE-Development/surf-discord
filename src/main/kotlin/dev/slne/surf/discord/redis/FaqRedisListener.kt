package dev.slne.surf.discord.redis

import dev.slne.surf.discord.faq.FaqPlatform
import dev.slne.surf.discord.faq.FaqSender
import dev.slne.surf.discord.faq.FaqService
import dev.slne.surf.moderation.tools.faq.redis.MinecraftFaqUsedEvent
import dev.slne.surf.redis.event.OnRedisEvent

object FaqRedisListener {
    @OnRedisEvent
    fun onMinecraftFaqUsed(event: MinecraftFaqUsedEvent) {
        val entry = FaqService.getActive(event.key, FaqPlatform.MINECRAFT) ?: return
        FaqService.recordUsage(entry, FaqPlatform.MINECRAFT, FaqSender(event.senderUuid, event.senderName))
    }
}
