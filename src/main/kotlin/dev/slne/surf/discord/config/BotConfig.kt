package dev.slne.surf.discord.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.ApiStatus
import kotlin.io.path.Path
import kotlin.io.path.exists
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

val botConfig by lazy { loadBotConfig() }

internal fun loadBotConfig(environment: Map<String, String> = System.getenv()): BotConfig {
    if ("BOT_TOKEN" !in environment) {
        return loadBotConfigFile()
    }

    return BotConfig(
        botToken = environment.require("BOT_TOKEN"),
        channels = ChannelConfig(
            ticketChannel = environment.requireLong("TICKET_CHANNEL_ID"),
            ticketLogChannel = environment.optionalLong("TICKET_LOG_CHANNEL_ID")
        ),
        database = DatabaseConfig(
            hostname = environment.require("DATABASE_HOST"),
            port = environment.optionalInt("DATABASE_PORT") ?: 3306,
            database = environment.require("DATABASE_NAME"),
            username = environment.require("DATABASE_USERNAME"),
            password = environment.require("DATABASE_PASSWORD")
        ),
        whitelistedRoleId = environment.optionalLong("WHITELISTED_ROLE_ID") ?: 0L,
        luckpermsApi = LuckpermsApiConfig(
            url = environment.optional("LUCKPERMS_API_URL") ?: "http://localhost:8080",
            token = environment.optional("LUCKPERMS_API_TOKEN").orEmpty()
        ),
        roles = RoleConfig(
            premiumRoleId = environment.optional("PREMIUM_ROLE_ID").orEmpty()
        )
    )
}

private fun loadBotConfigFile(): BotConfig {
    val configPath = Path("config.yml")
    check(configPath.exists()) {
        "Missing configuration: set BOT_TOKEN and the other required environment variables, " +
                "or provide ${configPath.toAbsolutePath()}"
    }

    return configPath.inputStream().use {
        Yaml(
            configuration = YamlConfiguration(
                strictMode = false
            )
        ).decodeFromStream<BotConfig>(it)
    }
}

private fun Map<String, String>.optional(name: String) = this[name]?.trim()?.takeIf(String::isNotEmpty)

private fun Map<String, String>.require(name: String) =
    optional(name) ?: error("Missing required environment variable: $name")

private fun Map<String, String>.optionalInt(name: String) = optional(name)?.toIntOrNull()
    ?: optional(name)?.let { error("Environment variable $name must be an integer") }

private fun Map<String, String>.optionalLong(name: String) = optional(name)?.toLongOrNull()
    ?: optional(name)?.let { error("Environment variable $name must be an integer") }

private fun Map<String, String>.requireLong(name: String) =
    optionalLong(name) ?: error("Missing required environment variable: $name")
