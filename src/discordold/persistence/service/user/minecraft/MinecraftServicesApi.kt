package dev.slne.discordold.persistence.service.user.minecraft

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(value = "minecraft-services-api-client", url = "https://api.minecraftservices.com")
interface MinecraftServicesApi {

    @GetMapping("/minecraft/profile/lookup/{uuid}")
    fun getUsername(@PathVariable uuid: UUID): MinecraftApiResponse

    @GetMapping("/minecraft/profile/lookup/name/{username}")
    fun getUuid(@PathVariable username: String): MinecraftApiResponse
}