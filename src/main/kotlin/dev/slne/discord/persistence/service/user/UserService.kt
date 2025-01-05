package dev.slne.discord.persistence.service.user

import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.coroutines.executeAsync
import java.io.Serial
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid


@OptIn(DelicateCoroutinesApi::class, ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
object UserService {
    private const val RATE_LIMIT_CODE = 429

    private val nameToUuidCache = Caffeine.newBuilder()
        .expireAfterWrite(1.hours.toJavaDuration())
        .buildAsync<String, UUID> { key, _ ->
            GlobalScope.future {
                try {
                    try {
                        getMinecraftApiUuid(key)
                    } catch (_: RateLimitException) {
                        Uuid.parseHex(getFallbackApiUuid(key).id).toJavaUuid()
                    }
                } catch (_: Exception) {
                    null
                }
            }
        }

    private val uuidToNameCache = Caffeine.newBuilder()
        .expireAfterWrite(1.hours.toJavaDuration())
        .buildAsync<UUID, String> { key, _ ->
            GlobalScope.future {
                try {
                    try {
                        getMinecraftApiUsername(key)
                    } catch (_: RateLimitException) {
                        getFallbackApiUuid(key).name
                    }
                } catch (_: Exception) {
                    null
                }
            }
        }

    private val client = OkHttpClient()

    suspend fun getUuidByUsername(
        username: String,
        context: CoroutineContext = Dispatchers.IO
    ): UUID? = withContext(context) { nameToUuidCache.get(username).await() }

    suspend fun getUsernameByUuid(
        uuid: UUID,
        context: CoroutineContext = Dispatchers.IO
    ): String? = withContext(context) { uuidToNameCache.get(uuid).await() }

    private suspend fun getMinecraftApiUsername(uuid: UUID): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://api.mojang.com/user/profile/${uuid}")
            .build()

        val response = client.newCall(request).executeAsync()
        if (response.code == RATE_LIMIT_CODE) throw RateLimitException("Rate limit reached for Minecraft API")
        if (!response.isSuccessful) throw Exception("Failed to get username from Minecraft API")

        val decoded = Json.decodeFromString<MinecraftApiUuidResponse>(response.body.string())
        response.close()

        decoded.name
    }

    private suspend fun getMinecraftApiUuid(username: String): UUID = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://api.mojang.com/users/profiles/minecraft/$username")
            .build()

        val response = client.newCall(request).executeAsync()
        if (response.code == RATE_LIMIT_CODE) throw RateLimitException("Rate limit reached for Minecraft API")
        if (!response.isSuccessful) throw Exception("Failed to get UUID from Minecraft API")

        val decoded = Json.decodeFromString<MinecraftApiUuidResponse>(response.body.string())
        response.close()

        Uuid.parseHex(decoded.id).toJavaUuid()
    }

    private suspend fun getFallbackApiUuid(usernameOrUuid: Any): MinecraftApiUuidResponse =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.minetools.eu/uuid/$usernameOrUuid")
                .build()

            val response = client.newCall(request).executeAsync()
            if (!response.isSuccessful) throw Exception("Failed to get UUID from fallback API")

            response.close()
            Json.decodeFromString<MinecraftApiUuidResponse>(response.body.string())
        }

    @Serializable
    private data class MinecraftApiUuidResponse(
        val id: String,
        val name: String
    )
}

class RateLimitException(message: String) : Exception(message) {
    companion object {
        @JvmStatic
        @Serial
        private val serialVersionUID: Long = -5712658221298095165L
    }
}