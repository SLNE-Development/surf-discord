package dev.slne.discordold.persistence.service.whitelist

import dev.slne.discordold.persistence.external.Whitelist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface WhitelistRepository : JpaRepository<Whitelist, Long> {
    @Transactional
    @Modifying
    @Query("delete from Whitelist w where w.uuid = :uuid or w.discordId = :discordId or w.twitchLink = :twitchLink")
    fun deleteWhitelists(uuid: UUID?, discordId: String?, twitchLink: String?)

    @Query("select w from Whitelist w where w.uuid = :uuid or w.discordId = :discordId or w.twitchLink = :twitchLink")
    fun findWhitelists(
        @Param("uuid") uuid: UUID?,
        @Param("discordId") discordId: String?,
        @Param("twitchLink") twitchLink: String?
    ): List<Whitelist>


    @Query("select (count(w) > 0) from Whitelist w where w.uuid = :uuid or w.discordId = :discordId or w.twitchLink = :twitchLink")
    fun isWhitelisted(
        @Param("uuid") uuid: UUID?,
        @Param("discordId") discordId: String?,
        @Param("twitchLink") twitchLink: String?
    ): Boolean


    @Query("select w from Whitelist w where w.discordId = :discordId")
    fun findByDiscordId(@Param("discordId") discordId: String): Whitelist?
}
