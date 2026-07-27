package dev.slne.surf.discord.config

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

object EnvConfig {
    private val env = dotenv {
        ignoreIfMissing = true
    }

    val BOT_TOKEN get() = env.require("BOT_TOKEN")

    val TICKET_CHANNEL get() = env.requireLong("TICKET_CHANNEL")
    val TICKET_LOG_CHANNEL get() = env.optionalLong("TICKET_LOG_CHANNEL")

    val LUCKPERMS_URL get() = env.optional("LUCKPERMS_URL")
    val LUCKPERMS_TOKEN get() = env.optional("LUCKPERMS_TOKEN")

    val PREMIUM_ROLE_ID get() = env.requireLong("PREMIUM_ROLE_ID")

    private fun Dotenv.optional(name: String) =
        get(name)?.trim()?.takeIf(String::isNotEmpty)

    private fun Dotenv.require(name: String) =
        optional(name) ?: error("Missing required environment variable: $name")

    private fun Dotenv.optionalLong(name: String) =
        optional(name)?.toLongOrNull()
            ?: optional(name)?.let { error("Environment variable $name must be a long") }

    private fun Dotenv.requireLong(name: String) =
        optionalLong(name) ?: error("Missing required environment variable: $name")

    private fun Dotenv.optionalInt(name: String) =
        optional(name)?.toIntOrNull()
            ?: optional(name)?.let { error("Environment variable $name must be an integer") }

    private fun Dotenv.requireInt(name: String) =
        optionalInt(name) ?: error("Missing required environment variable: $name")
}