package dev.slne.surf.discord.util

import com.github.benmanes.caffeine.cache.Caffeine
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Serializable
data class MojangProfile(val id: String, val name: String)

@Serializable
data class MinecraftServicesProfile(val id: String, val name: String)

@Serializable
data class MinetoolsResponse(val status: String, val id: String? = null)

@Service
class PlayerLookupService {
    private val client = HttpClient(CIO) {
        expectSuccess = false
    }

    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(30))
        .maximumSize(10_000)
        .build<String, UUID>()

    suspend fun getUuid(username: String): UUID? =
        cache.getIfPresent(username)
            ?: lookup(username)?.also { cache.put(username, it) }

    private suspend fun lookup(username: String): UUID? = coroutineScope {
        mojang(username) ?: minecraftServices(username) ?: minetools(username)
    }

    private suspend fun mojang(username: String): UUID? {
        val response = client.get("https://api.mojang.com/users/profiles/minecraft/$username")

        if (response.status != HttpStatusCode.OK) {
            return null
        }

        return undashedUuidToUuid(Json.decodeFromString<MojangProfile>(response.bodyAsText()).id)
    }

    private suspend fun minecraftServices(username: String): UUID? {
        val response =
            client.get("https://api.minecraftservices.com/minecraft/profile/lookup/name/$username")

        if (response.status != HttpStatusCode.OK) {
            return null
        }

        return undashedUuidToUuid(Json.decodeFromString<MinecraftServicesProfile>(response.bodyAsText()).id)
    }

    private suspend fun minetools(username: String): UUID? {
        val response = client.get("https://api.minetools.eu/uuid/$username")

        if (response.status != HttpStatusCode.OK) {
            return null
        }
        
        val body = Json.decodeFromString<MinetoolsResponse>(response.bodyAsText())
        return if (body.status == "OK") undashedUuidToUuid(body.id) else null
    }

    private fun undashedUuidToUuid(uuid: String?) = uuid?.let {
        UUID.fromString(
            it.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                "$1-$2-$3-$4-$5"
            )
        )
    }
}

