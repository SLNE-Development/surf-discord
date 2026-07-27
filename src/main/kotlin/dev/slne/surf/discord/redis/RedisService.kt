package dev.slne.surf.discord.redis

import dev.slne.surf.discord.discordMicroservice
import dev.slne.surf.discord.logger
import dev.slne.surf.redis.RedisApi
import dev.slne.surf.redis.StandaloneRedisInstance

object RedisService {
    lateinit var redisInstance: StandaloneRedisInstance
    lateinit var redisApi: RedisApi

    fun connect() {
        redisInstance = StandaloneRedisInstance("surf-discord", discordMicroservice.dataPath)
        redisInstance.create()

        redisApi = RedisApi.create()

        redisApi.registerRequestHandler(RedisRequestHandler)
        redisApi.freezeAndConnect()

        logger.info("Connected to redis")
    }

    fun disconnect() {
        if (::redisApi.isInitialized && redisApi.isConnected()) {
            redisApi.disconnect()
        }

        if (::redisInstance.isInitialized) {
            redisInstance.shutdown()
        }
    }
}