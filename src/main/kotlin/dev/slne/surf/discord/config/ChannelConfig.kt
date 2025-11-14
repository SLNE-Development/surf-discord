package dev.slne.surf.discord.config

import kotlinx.serialization.Serializable

@Serializable
data class ChannelConfig(
    val ticketChannel: Long,
    val ticketLogChannel: Long
)