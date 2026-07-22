package dev.slne.surf.discord.config

import kotlinx.serialization.Serializable

@Serializable
data class LuckpermsApiConfig(
    val url: String? = "http://localhost:8080",
    val token: String? = ""
)