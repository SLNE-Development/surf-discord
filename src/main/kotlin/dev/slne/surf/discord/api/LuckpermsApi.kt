package dev.slne.surf.discord.api

import dev.slne.surf.discord.config.botConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.util.*

object LuckpermsApi {
    private val logger = ComponentLogger.logger()

    fun isAvailable() =
        !botConfig.luckpermsApi.url.isNullOrBlank() && !botConfig.luckpermsApi.token.isNullOrBlank()

    private val client = HttpClient(CIO) {
        defaultRequest {

            if (!isAvailable()) {
                return@defaultRequest
            }

            url(botConfig.luckpermsApi.url)
            bearerAuth(botConfig.luckpermsApi.token ?: error("LuckPerms API token is not set"))
        }

        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun findAllPremiumUuids(): Set<UUID> {
        if (!isAvailable()) {
            return emptySet()
        }

        val response = client.get("/user/search") {
            parameter("group", "premium")
        }

        if (response.status != HttpStatusCode.OK) {
            logger.error("Failed to fetch premium UUIDs: ${response.status}")
            throw RuntimeException("Failed to fetch premium UUIDs")
        }

        val users = response.body<List<UserSearchResult>>()
        return users.mapTo(ObjectOpenHashSet(users.size)) { it.uniqueId }
    }

    @Serializable
    private data class UserSearchResult(
        val uniqueId: @Serializable(with = StringUuidSerializer::class) UUID,
        val results: List<Result>
    )

    @Serializable
    private data class Result(
        val key: String,
        val type: String,
        val value: Boolean,
        val context: List<String> = emptyList(),
        val expiry: Long? = null
    )

    private object StringUuidSerializer : KSerializer<UUID> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: UUID) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): UUID {
            return UUID.fromString(decoder.decodeString())
        }
    }
}