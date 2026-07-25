package dev.slne.surf.freebuild.whitelist.redis.request

import dev.slne.surf.redis.request.RedisRequest
import kotlinx.serialization.Serializable

@Serializable
data class DiscordMemberShipRequest(
    val discordUserId: Long
) : RedisRequest()
