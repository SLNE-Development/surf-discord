package dev.slne.discordold.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus
import kotlin.io.path.Path
import kotlin.io.path.inputStream

@ApiStatus.Internal
@Serializable
data class BotConfig(
    @SerialName("bot-token") val botToken: String,
    val database: DatabaseConfig
)

val botConfig by lazy {
    Path("data/config.yml").inputStream().use { Yaml.default.decodeFromStream<BotConfig>(it) }
}