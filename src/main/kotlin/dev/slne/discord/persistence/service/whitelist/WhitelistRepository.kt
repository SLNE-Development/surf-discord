package dev.slne.discord.persistence.service.whitelist

import dev.slne.discord.persistence.external.Whitelist
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface WhitelistRepository : CoroutineCrudRepository<Whitelist, Long> {

    fun findByDiscordId(discordId: String): Whitelist?

    @Transactional
    @Modifying
    @Query("delete from Whitelist w where w.uuid = ?1 or w.discordId = ?2 or w.twitchLink = ?3")
    fun deleteWhitelists(uuid: UUID?, discordId: String?, twitchLink: String?)

    @Transactional
    @Query("select w from Whitelist w where w.uuid = ?1 or w.discordId = ?2 or w.twitchLink = ?3")
    fun findWhitelists(uuid: UUID?, discordId: String?, twitchLink: String?): List<Whitelist>

    fun findWhitelistByDiscordId(discordId: String): Whitelist?
}
