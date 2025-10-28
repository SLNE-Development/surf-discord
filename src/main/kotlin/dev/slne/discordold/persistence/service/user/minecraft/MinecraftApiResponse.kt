package dev.slne.discordold.persistence.service.user.minecraft

import java.util.*

/**
 * {
 *   "id": "5c63e51b82b14222af0f66a4c31e36ad",
 *   "name": "NotAmmo"
 * }
 */
data class MinecraftApiResponse(
    val id: String,
    val name: String,
) {
    val uuid: UUID = UUID.fromString(
        id.substring(0, 8) + "-" +
                id.substring(8, 12) + "-" +
                id.substring(12, 16) + "-" +
                id.substring(16, 20) + "-" +
                id.substring(20)
    )
}
