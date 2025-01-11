package dev.slne.discord.persistence.service.user.minetools

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * {
 *   "cache": {
 *     "HIT": false,
 *     "cache_time": 518400,
 *     "cache_time_left": 518399,
 *     "cached_at": 1736343742.7480717,
 *     "cached_until": 1736862142.7480717
 *   },
 *   "id": "5c63e51b82b14222af0f66a4c31e36ad",
 *   "name": "NotAmmo",
 *   "status": "OK"
 * }
 */
data class MinetoolsApiResponse(
    val id: String,
    val name: String,
    val status: String,
    val cache: MinetoolsApiResponseCache,
) {
    val uuid: UUID = UUID.fromString(
        id.substring(0, 8) + "-" +
                id.substring(8, 12) + "-" +
                id.substring(12, 16) + "-" +
                id.substring(16, 20) + "-" +
                id.substring(20)
    )

    /**
     * "cache": {
     *     "HIT": false,
     *     "cache_time": 518400,
     *     "cache_time_left": 518399,
     *     "cached_at": 1736343742.7480717,
     *     "cached_until": 1736862142.7480717
     *   }
     */
    data class MinetoolsApiResponseCache(
        @JsonProperty("HIT")
        val hit: Boolean,

        @JsonProperty("cache_time")
        val cacheTime: Long,

        @JsonProperty("cache_time_left")
        val cacheTimeLeft: Long?,

        @JsonProperty("cached_at")
        val cachedAt: Double,

        @JsonProperty("cached_until")
        val cachedUntil: Double,
    )
}
