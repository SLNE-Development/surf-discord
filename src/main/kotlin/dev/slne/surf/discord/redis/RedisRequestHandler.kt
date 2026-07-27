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
    fun handleDiscordMembershipRequest(context: RequestContext<DiscordMemberShipRequest>) =
        context.launch(Dispatchers.IO) {
            val discordUserId = context.request.discordUserId

            context.respond(
                DiscordMemberShipResponse(
                    memberResultCache.getIfPresent(discordUserId) ?: jda.guilds.any {
                        val member = runCatching {
                            it.retrieveMemberById(discordUserId).await()
                        }.getOrNull()

                        member?.let { memberResultCache.put(discordUserId, true) }
                        member != null
                    })
            )
        }
}