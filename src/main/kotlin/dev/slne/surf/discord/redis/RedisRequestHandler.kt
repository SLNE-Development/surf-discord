package dev.slne.surf.discord.redis

import com.github.benmanes.caffeine.cache.Caffeine
import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.jda
import dev.slne.surf.freebuild.whitelist.redis.request.DiscordMemberShipRequest
import dev.slne.surf.freebuild.whitelist.redis.request.DiscordMemberShipResponse
import dev.slne.surf.redis.request.HandleRedisRequest
import dev.slne.surf.redis.request.RequestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object RedisRequestHandler {
    private val memberResultCache = Caffeine.newBuilder().build<Long, Boolean>()

    @HandleRedisRequest
    fun handleDiscordMembershipRequest(context: RequestContext<DiscordMemberShipRequest>) {
        val discordUserId = context.request.discordUserId

        context.launch(Dispatchers.IO) {
            context.respond(
                DiscordMemberShipResponse(
                    memberResultCache.getIfPresent(discordUserId) ?: jda.guilds.any {
                        runCatching {
                            it.retrieveMemberById(discordUserId).await()
                        }.getOrNull() != null
                    })
            )
        }
    }
}