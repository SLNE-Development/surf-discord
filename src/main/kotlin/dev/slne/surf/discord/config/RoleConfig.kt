package dev.slne.surf.discord.config

import kotlinx.serialization.Serializable

@Serializable
data class RoleConfig(val premiumRoleId: Long = 0L)
