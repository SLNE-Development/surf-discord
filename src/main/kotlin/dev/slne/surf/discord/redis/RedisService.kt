package dev.slne.surf.discord.redis

import dev.slne.surf.discord.discordMicroservice
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.redis.roles.RedisDiscordRolesConsumer
import dev.slne.surf.redis.RedisApi
import dev.slne.surf.redis.StandaloneRedisInstance

object RedisService {
    lateinit var redisInstance: StandaloneRedisInstance
    lateinit var redisApi: RedisApi
    lateinit var discordRolesHandler: RedisDiscordRolesConsumer
        private set

    fun connect() {
        redisInstance = StandaloneRedisInstance("surf-discord", discordMicroservice.dataPath)
        redisInstance.create()

        redisApi = RedisApi.create()

        redisApi.registerRequestHandler(RedisRequestHandler)
        redisApi.freezeAndConnect()

        discordRolesHandler = RedisDiscordRolesConsumer(redisApi)
        discordRolesHandler.startConsuming()

        logger.info("Connected to redis")
    }

    fun disconnect() {
        if (::discordRolesHandler.isInitialized) {
            discordRolesHandler.stopConsuming()
        }

        if (::redisApi.isInitialized && redisApi.isConnected()) {
            redisApi.disconnect()
        }

        if (::redisInstance.isInitialized) {
            redisInstance.shutdown()
        }
    }
}