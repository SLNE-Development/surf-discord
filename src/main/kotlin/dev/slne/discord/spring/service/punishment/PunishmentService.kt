package dev.slne.discord.spring.service.punishment

import feign.FeignException

@org.springframework.stereotype.Service
class PunishmentService @Autowired constructor(private val punishmentClient: dev.slne.discord.spring.feign.client.PunishmentClient) {
    /**
     * Gets ban by punishment id.
     *
     * @param punishmentId the punishment id
     * @return the ban by punishment id
     */
    @org.springframework.scheduling.annotation.Async
    fun getBanByPunishmentId(punishmentId: String?): java.util.concurrent.CompletableFuture<dev.slne.discord.spring.feign.dto.PunishmentBanDTO?> {
        return try {
            java.util.concurrent.CompletableFuture.completedFuture<dev.slne.discord.spring.feign.dto.PunishmentBanDTO?>(
                punishmentClient.getBanByPunishmentId(punishmentId)
            )
        } catch (ignored: FeignException.NotFound) {
            java.util.concurrent.CompletableFuture.completedFuture<dev.slne.discord.spring.feign.dto.PunishmentBanDTO?>(
                null
            )
        }
    }

    /**
     * Is valid punishment id completable future.
     *
     * @param punishmentId the punishment id
     * @return the completable future
     */
    @org.springframework.scheduling.annotation.Async
    fun isValidPunishmentId(punishmentId: String?): java.util.concurrent.CompletableFuture<Boolean> {
        return java.util.concurrent.CompletableFuture.completedFuture(
            getBanByPunishmentId(
                punishmentId
            ).join() != null
        )
    }
}
