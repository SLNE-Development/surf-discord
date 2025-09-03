package dev.slne.discord.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus
import kotlin.io.path.Path
import kotlin.io.path.inputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@ApiStatus.Internal
@Serializable
data class BotConfig(
    @SerialName("bot-token") val botToken: String,
    val database: DatabaseConfig,
    val cooldown: CooldownConfig = CooldownConfig()
) {
    @Serializable
    data class CooldownConfig(
        @SerialName("faq-cooldown")
        val faqCooldown: Duration = 10.seconds
    )
}

val botConfig by lazy {
    Path("data/config.yml").inputStream().use { Yaml.default.decodeFromStream<BotConfig>(it) }
}