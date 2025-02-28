package dev.slne.discord.persistence.service.user

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.discord.persistence.service.user.minecraft.MinecraftApiClient
import dev.slne.discord.persistence.service.user.minetools.MinetoolsApiClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@OptIn(DelicateCoroutinesApi::class)
@Service
class UserService(
    private val minecraftApiClient: MinecraftApiClient,
    private val minetoolsApiClient: MinetoolsApiClient
) {

    private val nameToUuidCache = Caffeine.newBuilder()
        .expireAfterWrite(1.hours.toJavaDuration())
        .buildAsync<String, UUID> { key, _ ->
            GlobalScope.future {
                try {
                    minecraftApiClient.getUuid(key).uuid
                } catch (_: Exception) {
                    try {
                        minetoolsApiClient.getUuid(key).uuid
                    } catch (_: Exception) {
                        null
                    }
                }
            }
        }

    private val uuidToNameCache = Caffeine.newBuilder()
        .expireAfterWrite(1.hours.toJavaDuration())
        .buildAsync<UUID, String> { key, _ ->
            GlobalScope.future {
                try {
                    minecraftApiClient.getUsername(key).name
                } catch (_: Exception) {
                    try {
                        minetoolsApiClient.getUsername(key).name
                    } catch (_: Exception) {
                        null
                    }
                }
            }
        }

    suspend fun getUuidByUsername(
        username: String,
        context: CoroutineContext = Dispatchers.IO
    ): UUID? = withContext(context) { nameToUuidCache.get(username).await() }

    suspend fun getUsernameByUuid(
        uuid: UUID,
        context: CoroutineContext = Dispatchers.IO
    ): String? = withContext(context) { uuidToNameCache.get(uuid).await() }
}