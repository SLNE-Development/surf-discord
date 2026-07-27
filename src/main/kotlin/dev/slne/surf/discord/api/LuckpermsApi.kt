package dev.slne.surf.discord.api

import dev.slne.surf.discord.config.botConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
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
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.util.*

object LuckpermsApi {
    private const val PREMIUM_GROUP = "premium"

    private val logger = ComponentLogger.logger()

    fun isAvailable(): Boolean {
        val config = botConfig.luckpermsApi

        return !config.url.isNullOrBlank() && !config.token.isNullOrBlank()
    }

    private val client = HttpClient(OkHttp) {
        expectSuccess = true

        defaultRequest {
            val config = botConfig.luckpermsApi

            val baseUrl = config.url
                ?.trim()
                ?.takeIf(String::isNotEmpty)
                ?: return@defaultRequest

            val token = config.token
                ?.trim()
                ?.takeIf(String::isNotEmpty)
                ?: return@defaultRequest


            url.takeFrom(baseUrl.trimEnd('/') + '/')

            bearerAuth(token)
            accept(ContentType.Application.Json)
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 5_000
            requestTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
        }
    }

    suspend fun findAllPremiumUuids(): Set<UUID> {
        if (!isAvailable()) {
            return emptySet()
        }

        val users = try {
            client.get("user/search") {
                parameter("group", PREMIUM_GROUP)
            }.body<List<UserSearchResult>>()
        } catch (exception: ResponseException) {
            logger.error(
                "Failed to fetch premium users from the LuckPerms API: {}",
                exception.response.status,
                exception
            )

            throw exception
        }

        return users.mapTo(ObjectOpenHashSet(users.size)) {
            it.uniqueId
        }
    }

    @Serializable
    private data class UserSearchResult(
        @Serializable(with = StringUuidSerializer::class)
        val uniqueId: UUID,
    )

    private object StringUuidSerializer : KSerializer<UUID> {
        override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: UUID) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): UUID {
            return UUID.fromString(decoder.decodeString())
        }
    }
}