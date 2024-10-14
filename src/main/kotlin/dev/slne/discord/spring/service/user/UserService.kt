package dev.slne.discord.spring.service.user

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
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid


@OptIn(DelicateCoroutinesApi::class, ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
object UserService {
    private const val RATE_LIMIT_CODE = 429

    private val uuidToNameCache = Caffeine.newBuilder()
        .expireAfterWrite(1.days.toJavaDuration())
        .buildAsync<String, UUID> { key, _ ->
            GlobalScope.future {
                try {
                    try {
                        getMinecraftApiUuid(key)
                    } catch (e: RateLimitException) {
                        getFallbackApiUuid(key)
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    private val client = OkHttpClient()

    suspend fun getUuidByUsername(
        username: String,
        context: CoroutineContext = Dispatchers.IO
    ): UUID? = withContext(context) { uuidToNameCache.get(username).await() }

    private suspend fun getMinecraftApiUuid(username: String): UUID = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://api.mojang.com/users/profiles/minecraft/$username")
            .build()

        val response = client.newCall(request).executeAsync()
        if (response.code == RATE_LIMIT_CODE) throw RateLimitException("Rate limit reached for Minecraft API")
        if (!response.isSuccessful) throw Exception("Failed to get UUID from Minecraft API")

        val decoded = Json.decodeFromString<MinecraftApiUuidResponse>(response.body.string())
        Uuid.parseHex(decoded.id).toJavaUuid()
    }

    private suspend fun getFallbackApiUuid(username: String): UUID = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://api.minetools.eu/uuid/$username")
            .build()

        val response = client.newCall(request).executeAsync()
        if (!response.isSuccessful) throw Exception("Failed to get UUID from fallback API")

        val decoded =
            Json.decodeFromString<MinecraftApiUuidResponse>(response.body.string())// TODO: 14.10.2024 18:24 - testme
        Uuid.parseHex(decoded.id).toJavaUuid()
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