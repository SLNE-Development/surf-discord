package dev.slne.discordold.persistence.service.user.minetools

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(value = "minetools-api-client", url = "https://api.minetools.eu")
interface MinetoolsApiClient {

    @GetMapping("/uuid/{username}")
    fun getUuid(@PathVariable username: String): MinetoolsApiResponse

    @GetMapping("/uuid/{uuid}")
    fun getUsername(@PathVariable uuid: UUID): MinetoolsApiResponse
}