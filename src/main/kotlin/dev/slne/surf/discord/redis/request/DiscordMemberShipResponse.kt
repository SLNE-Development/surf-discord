package dev.slne.surf.freebuild.whitelist.redis.request

import dev.slne.surf.redis.request.RedisResponse
import kotlinx.serialization.Serializable

@Serializable
data class DiscordMemberShipResponse(
    val isMember: Boolean
) : RedisResponse()
