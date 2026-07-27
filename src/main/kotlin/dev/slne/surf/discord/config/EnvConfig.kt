package dev.slne.surf.discord.config

import dev.slne.surf.api.core.environment.env

object EnvConfig {
    val BOT_TOKEN by env.required(sensitive = true)
    val TICKET_CHANNEL by env.long()
    val TICKET_LOG_CHANNEL by env.optionalLong()

    val LUCKPERMS_URL by env.optional()
    val LUCKPERMS_TOKEN by env.optional(sensitive = true)

    val PREMIUM_ROLE_ID by env.long()
}