package dev.slne.surf.discord.config

import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
@Serializable
data class BotConfig(
    val botToken: String,
    val channels: ChannelConfig,
    val luckpermsApi: LuckpermsApiConfig = LuckpermsApiConfig(),
    val roles: RoleConfig = RoleConfig()
)

val botConfig by lazy {
    BotConfig(
        EnvConfig.BOT_TOKEN,
        ChannelConfig(
            EnvConfig.TICKET_CHANNEL,
            EnvConfig.TICKET_LOG_CHANNEL,
        ),
        LuckpermsApiConfig(
            EnvConfig.LUCKPERMS_URL,
            EnvConfig.LUCKPERMS_TOKEN
        ),
        RoleConfig(
            EnvConfig.PREMIUM_ROLE_ID
        )
    )
}