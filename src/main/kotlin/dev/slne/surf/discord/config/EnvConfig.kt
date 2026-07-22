package dev.slne.surf.discord.config

object EnvConfig {
    private val env by lazy {
        System.getenv()
    }

    val BOT_TOKEN get() = env.require("BOT_TOKEN")

    val TICKET_CHANNEL get() = env.requireLong("TICKET_CHANNEL")
    val TICKET_LOG_CHANNEL get() = env.optionalLong("TICKET_LOG_CHANNEL")

    val DB_HOST get() = env.require("DATABASE_HOST")
    val DB_PORT get() = env.requireInt("DATABASE_PORT")
    val DB_NAME get() = env.require("DATABASE_NAME")
    val DB_USERNAME get() = env.require("DATABASE_USERNAME")
    val DB_PASSWORD get() = env.require("DATABASE_PASSWORD")

    val LUCKPERMS_URL get() = env.optional("LUCKPERMS_URL")
    val LUCKPERMS_TOKEN get() = env.optional("LUCKPERMS_TOKEN")

    val WHITELIST_ROLE_ID get() = env.requireLong("WHITELIST_ROLE_ID")
    val PREMIUM_ROLE_ID get() = env.requireLong("PREMIUM_ROLE_ID")

    private fun Map<String, String>.optional(name: String) =
        this[name]?.trim()?.takeIf(String::isNotEmpty)

    private fun Map<String, String>.require(name: String) =
        optional(name) ?: error("Missing required environment variable: $name")

    private fun Map<String, String>.optionalLong(name: String) = optional(name)?.toLongOrNull()
        ?: optional(name)?.let { error("Environment variable $name must be an integer") }

    private fun Map<String, String>.requireLong(name: String) =
        optionalLong(name) ?: error("Missing required environment variable: $name")

    private fun Map<String, String>.optionalInt(name: String) = optional(name)?.toIntOrNull()
        ?: optional(name)?.let { error("Environment variable $name must be an integer") }

    private fun Map<String, String>.requireInt(name: String) =
        optionalInt(name) ?: error("Missing required environment variable: $name")
}