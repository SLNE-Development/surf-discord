package dev.slne.discordold.persistence.service.user.minecraft

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(value = "minecraft-api-client", url = "https://api.mojang.com")
interface MojangApiClient {

    @GetMapping("/user/profile/{uuid}")
    fun getUsername(@PathVariable uuid: UUID): MinecraftApiResponse

    @GetMapping("/users/profiles/minecraft/{username}")
    fun getUuid(@PathVariable username: String): MinecraftApiResponse
}