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
import java.time.Duration
import java.util.*

private val json = Json { ignoreUnknownKeys = true }

@Serializable
data class MojangProfile(val id: String, val name: String)

@Serializable
data class MinecraftServicesProfile(val id: String, val name: String)

@Serializable
data class MinetoolsResponse(val status: String, val id: String? = null, val name: String? = null)

object PlayerLookupService {
    private val client = HttpClient(CIO) {
        expectSuccess = false
    }

    private val cacheUuid = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(30))
        .maximumSize(10_000)
        .build<String, UUID>()

    private val cacheName = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(30))
        .maximumSize(10_000)
        .build<UUID, String>()

    suspend fun getUuid(username: String): UUID? =
        cacheUuid.getIfPresent(username)
            ?: lookup(username)?.also {
                cacheUuid.put(username, it)
                cacheName.put(it, username)
            }

    suspend fun getUsername(uuid: UUID): String? =
        cacheName.getIfPresent(uuid)
            ?: reverseLookup(uuid)?.also { cacheName.put(uuid, it) }

    private suspend fun lookup(username: String): UUID? = coroutineScope {
        mojang(username) ?: minecraftServices(username) ?: minetools(username)
    }

    private suspend fun reverseLookup(uuid: UUID): String? = coroutineScope {
        mojangByUuid(uuid) ?: minecraftServicesByUuid(uuid) ?: minetoolsByUuid(uuid)
    }

    private suspend fun mojang(username: String): UUID? {
        val response = client.get("https://api.mojang.com/users/profiles/minecraft/$username")
        if (response.status != HttpStatusCode.OK) return null
        return undashedUuidToUuid(json.decodeFromString<MojangProfile>(response.bodyAsText()).id)
    }

    private suspend fun mojangByUuid(uuid: UUID): String? {
        val response =
            client.get(
                "https://sessionserver.mojang.com/session/minecraft/profile/${
                    uuid.toString().replace("-", "")
                }"
            )
        if (response.status != HttpStatusCode.OK) return null
        return json.decodeFromString<MojangProfile>(response.bodyAsText()).name
    }

    private suspend fun minecraftServices(username: String): UUID? {
        val response =
            client.get("https://api.minecraftservices.com/minecraft/profile/lookup/name/$username")
        if (response.status != HttpStatusCode.OK) return null
        return undashedUuidToUuid(json.decodeFromString<MinecraftServicesProfile>(response.bodyAsText()).id)
    }

    private suspend fun minecraftServicesByUuid(uuid: UUID): String? {
        val response =
            client.get("https://api.minecraftservices.com/minecraft/profile/$uuid")
        if (response.status != HttpStatusCode.OK) return null
        return json.decodeFromString<MinecraftServicesProfile>(response.bodyAsText()).name
    }

    private suspend fun minetools(username: String): UUID? {
        val response = client.get("https://api.minetools.eu/uuid/$username")
        if (response.status != HttpStatusCode.OK) return null
        val body = json.decodeFromString<MinetoolsResponse>(response.bodyAsText())
        return if (body.status == "OK") undashedUuidToUuid(body.id) else null
    }

    private suspend fun minetoolsByUuid(uuid: UUID): String? {
        val response =
            client.get("https://api.minetools.eu/profile/${uuid.toString().replace("-", "")}")
        if (response.status != HttpStatusCode.OK) return null
        val body = json.decodeFromString<MinetoolsResponse>(response.bodyAsText())
        return if (body.status == "OK") body.name else null
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
