package dev.slne.discord.persistence.service.whitelist

import dev.slne.discord.persistence.external.Whitelist
import dev.slne.discord.persistence.sessionFactory
import dev.slne.discord.persistence.upsert
import dev.slne.discord.persistence.withSession
import jakarta.persistence.criteria.Predicate
import net.dv8tion.jda.api.entities.User
import java.util.*

object WhitelistRepository {

    suspend fun saveWhitelist(whitelist: Whitelist): Whitelist = sessionFactory.withSession {
        it.upsert(whitelist) { id != null }
    }

    suspend fun getWhitelistByDiscordId(discordId: String): Whitelist? =
        sessionFactory.withSession { session ->
            val criteriaBuilder = session.criteriaBuilder

            val query = criteriaBuilder.createQuery(Whitelist::class.java)
            val root = query.from(Whitelist::class.java)
            query.where(criteriaBuilder.equal(root.get<String>("discordId"), discordId))
            
            session.createQuery(query.select(root)).resultList.firstOrNull()
        }

    suspend fun findWhitelists(
        uuid: UUID? = null,
        discordId: String? = null,
        twitchLink: String? = null
    ): List<Whitelist> = sessionFactory.withSession { session ->
        val criteriaBuilder = session.criteriaBuilder
        val query = criteriaBuilder.createQuery(Whitelist::class.java)
        val root = query.from(Whitelist::class.java)
        val predicates = mutableListOf<Predicate>()

        if (uuid != null) {
            predicates.add(criteriaBuilder.equal(root.get<UUID>("uuid"), uuid))
        }

        if (discordId != null) {
            predicates.add(criteriaBuilder.equal(root.get<String>("discordId"), discordId))
        }

        if (twitchLink != null) {
            predicates.add(
                criteriaBuilder.equal(
                    root.get<String>("twitchLink"),
                    twitchLink
                )
            )
        }

        if (predicates.isEmpty()) {
            return@withSession emptyList()
        }

        query.where(criteriaBuilder.or(*predicates.toTypedArray()))

        val createQuery = session.createQuery(query.select(root))
        createQuery.resultList
    }

    suspend fun isWhitelisted(
        uuid: UUID?,
        discordId: String?,
        twitchLink: String?
    ): Boolean = findWhitelists(uuid, discordId, twitchLink).isNotEmpty()

    suspend fun isWhitelisted(user: User): Boolean = isWhitelisted(null, user.id, null)
}
