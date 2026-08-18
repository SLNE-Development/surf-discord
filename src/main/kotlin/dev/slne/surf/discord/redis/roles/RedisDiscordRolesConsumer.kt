package dev.slne.surf.discord.redis.roles

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.discordScope
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.logger
import dev.slne.surf.redis.RedisApi
import dev.slne.surf.redis.libs.redisson.api.stream.StreamMessageId
import dev.slne.surf.redis.libs.redisson.api.stream.StreamReadArgs
import dev.slne.surf.redis.libs.redisson.client.codec.StringCodec
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.requests.ErrorResponse
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class RedisDiscordRolesConsumer(private val redisApi: RedisApi) {

    companion object {
        private const val POLL_BATCH_SIZE = 50
        private val POLL_INTERVAL = 250.milliseconds
        private val POLL_ERROR_BACKOFF = 2.seconds

        private val STREAM_START = StreamMessageId(0, 0)

        private val messageIdComparator = compareBy<Map.Entry<StreamMessageId, *>>({ it.key.id0 }, { it.key.id1 })
        private val missingMemberResponses = EnumSet.of(ErrorResponse.UNKNOWN_MEMBER, ErrorResponse.UNKNOWN_USER)
    }

    private val json = Json(redisApi.json) {
        ignoreUnknownKeys = true
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    private val stream by lazy {
        redisApi.redisson.getStream<String, String>(
            "surf-discord:discord-roles:whitelisted-roles", // Never changes this without a good reason. It is used across multiple services, and changing it will break things.
            StringCodec.INSTANCE
        )
    }

    private var pollJob: Job? = null

    fun startConsuming() {
        if (pollJob?.isActive == true) {
            logger.warn("Discord roles stream is already being consumed.")
            return
        }

        pollJob = discordScope.launch(CoroutineName("discord-roles-stream-consumer")) {
            while (isActive) {
                val delayUntilNextPoll = try {
                    poll()
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (throwable: Throwable) {
                    logger.error("Failed to poll the discord roles stream.", throwable)
                    POLL_ERROR_BACKOFF
                }

                delay(delayUntilNextPoll)
            }
        }
    }

    fun stopConsuming() {
        pollJob?.cancel("Discord roles stream consumption stopped.")
        pollJob = null
    }

    private suspend fun poll(): Duration {
        val args = StreamReadArgs
            .greaterThan(STREAM_START)
            .count(POLL_BATCH_SIZE)

        val batch = stream.readAsync(args).await()
        if (batch.isNullOrEmpty()) return POLL_INTERVAL

        val processed = ObjectArrayList<StreamMessageId>()
        var failed = false

        for ((messageId, fields) in batch.entries.sortedWith(messageIdComparator)) {
            try {
                handleEntry(messageId, fields)
                processed += messageId
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                logger.error("Failed to handle discord roles request $messageId.", throwable)
                failed = true
                break
            }
        }

        if (processed.isNotEmpty()) {
            stream.remove(*processed.toTypedArray())
        }

        return if (failed) POLL_ERROR_BACKOFF else POLL_INTERVAL
    }

    private suspend fun handleEntry(messageId: StreamMessageId, fields: Map<String, String>) {
        val rawRequest = fields.values.firstOrNull()

        if (rawRequest == null) {
            logger.warn("Dropping discord roles request $messageId without any value.")
            return
        }

        val request = try {
            json.decodeFromString<Request>(rawRequest)
        } catch (exception: IllegalArgumentException) {
            logger.warn("Dropping undecodable discord roles request $messageId: $rawRequest", exception)
            return
        }

        handleRequest(request)
    }

    private suspend fun handleRequest(request: Request) {
        val roleId = botConfig.roles.whitelistedRoleId
        var guildsWithRole = 0

        for (guild in jda.guilds) {
            val role = guild.getRoleById(roleId) ?: continue
            guildsWithRole++

            val member = retrieveMember(guild, request.userId)

            if (member == null) {
                logger.warn("Skipping ${request.action} of the whitelisted role: user ${request.userId} is not a member of guild ${guild.id}")
                continue
            }

            when (request.action) {
                Request.Action.ADD -> {
                    if (role in member.roles) continue

                    guild.addRoleToMember(member, role).reason("Whitelisted").await()
                    logger.info("Added the whitelisted role to user ${request.userId} in guild ${guild.id}")
                }

                Request.Action.REMOVE -> {
                    if (role !in member.roles) continue

                    guild.removeRoleFromMember(member, role).reason("No longer whitelisted").await()
                    logger.info("Removed the whitelisted role from user ${request.userId} in guild ${guild.id}")
                }
            }
        }

        check(guildsWithRole > 0) { "Whitelisted role $roleId does not exist in any guild the bot is in" }
    }

    private suspend fun retrieveMember(guild: Guild, userId: Long): Member? = try {
        guild.retrieveMemberById(userId).await()
    } catch (exception: ErrorResponseException) {
        if (exception.errorResponse !in missingMemberResponses) throw exception
        null
    }

    @Serializable
    data class Request(
        val action: Action,
        val userId: Long
    ) {
        enum class Action {
            ADD,
            REMOVE
        }
    }
}
