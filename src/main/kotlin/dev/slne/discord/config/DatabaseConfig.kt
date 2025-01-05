package dev.slne.discord.config

import org.jetbrains.annotations.ApiStatus
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ApiStatus.Internal
@ConfigSerializable
data class DatabaseConfig(
    val hostname: String? = null,
    val port: Int? = 3306,
    val database: String? = null,
    val username: String? = null,
    val password: String? = null
)