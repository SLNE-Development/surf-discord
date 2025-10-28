package dev.slne.surf.discord.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus
import kotlin.io.path.Path
import kotlin.io.path.inputStream

@ApiStatus.Internal
@Serializable
data class BotConfig(
    val botToken: String,
    val database: DatabaseConfig
)

val botConfig by lazy {
    Path("config.yml").inputStream().use { Yaml.default.decodeFromStream<BotConfig>(it) }
}