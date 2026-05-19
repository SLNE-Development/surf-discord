package dev.slne.surf.discord.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus
import kotlin.io.path.Path
import kotlin.io.path.inputStream

@ApiStatus.Internal
@Serializable
data class BotConfig(
    val botToken: String,
    val channels: ChannelConfig,
    val database: DatabaseConfig,
    val whitelistedRoleId: Long = 0L,
    val luckpermsApi: LuckpermsApiConfig = LuckpermsApiConfig(),
    val roles: RoleConfig = RoleConfig()
)

val botConfig by lazy {
    Path("config.yml").inputStream().use {
        Yaml(
            configuration = YamlConfiguration(
                strictMode = false
            )
        ).decodeFromStream<BotConfig>(it)
    }
}