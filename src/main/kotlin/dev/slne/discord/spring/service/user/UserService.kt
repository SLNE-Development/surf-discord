@file:OptIn(ExperimentalUuidApi::class)

package dev.slne.discord.spring.service.user

import khttp.get
import khttp.responses.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Serial
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

object UserService {
    private const val RATE_LIMIT_CODE = 429

    suspend fun getUsernameByUuid(
        uuid: UUID,
        context: CoroutineContext = Dispatchers.IO
    ): String = withContext(context) {
        try {
            val minecraftUsername = getMinecraftApiUsername(uuid)
            minecraftUsername ?: getFallbackApiUsername(uuid)
        } catch (e: RateLimitException) {
            getFallbackApiUsername(uuid)
        }
    }

    suspend fun getUuidByUsername(
        username: String,
        context: CoroutineContext = Dispatchers.IO
    ): UUID = withContext(context) {
        try {
            val minecraftUuid = getMinecraftApiUuid(username)
            minecraftUuid ?: getFallbackApiUuid(username)
        } catch (e: RateLimitException) {
            getFallbackApiUuid(username)
        }
    }

    private suspend fun getMinecraftApiUsername(uuid: UUID): String? = withContext(Dispatchers.IO) {
        get(url = "https://api.mojang.com/user/profile/$uuid").run {
            if (statusCode == RATE_LIMIT_CODE) throw RateLimitException("Rate limit reached for Minecraft API")
            jsonObject.getString("name")
        }
    }

    private suspend fun getFallbackApiUsername(uuid: UUID): String = withContext(Dispatchers.IO) {
        get(url = "https://api.minetools.eu/uuid/$uuid").run {
            if (!isSuccessful) throw Exception("Failed to get username from fallback API")
            jsonObject.getString("name")
        }
    }

    private suspend fun getMinecraftApiUuid(username: String): UUID? = withContext(Dispatchers.IO) {
        get(url = "https://api.mojang.com/users/profiles/minecraft/$username").run {
            if (statusCode == RATE_LIMIT_CODE) throw RateLimitException("Rate limit reached for Minecraft API")
            Uuid.parseHex(jsonObject.getString("id")).toJavaUuid()
        }
    }

    private suspend fun getFallbackApiUuid(username: String): UUID = withContext(Dispatchers.IO) {
        get(url = "https://api.minetools.eu/uuid/$username").run {
            if (!isSuccessful) throw Exception("Failed to get UUID from fallback API")
            Uuid.parseHex(jsonObject.getString("id")).toJavaUuid()
        }
    }

    private val Response.isSuccessful: Boolean
        get() = this.statusCode in 200..299
}

class RateLimitException(message: String) : Exception(message) {
    companion object {
        @JvmStatic
        @Serial
        private val serialVersionUID: Long = -5712658221298095165L
    }
}