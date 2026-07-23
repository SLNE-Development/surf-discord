package dev.slne.surf.discord.ticket.database.migration

import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.core.vendors.currentDialect
import org.jetbrains.exposed.v1.r2dbc.transactions.TransactionManager

/**
 * Repairs business-key constraints required by PostgreSQL upserts on databases created before the
 * explicit conflict targets were introduced.
 *
 * Existing duplicate rows are reduced to the row with the highest technical ID before the unique
 * indexes are created. The migration is safe to execute on every startup.
 */
suspend fun migratePostgreSqlUpsertConstraints() {
    if (currentDialect !is PostgreSQLDialect) return

    val transaction = TransactionManager.current()

    transaction.exec(
        """
        DELETE FROM "surf-discord".ticket_members AS older
        USING "surf-discord".ticket_members AS newer
        WHERE older.ticket_id = newer.ticket_id
          AND older.member_id = newer.member_id
          AND older.id < newer.id
        """.trimIndent()
    )

    transaction.exec(
        """
        CREATE UNIQUE INDEX IF NOT EXISTS ticket_members_ticket_id_member_id_uq_idx
        ON "surf-discord".ticket_members (ticket_id, member_id)
        """.trimIndent()
    )

    transaction.exec(
        """
        DELETE FROM "surf-discord".ticket_deadline_notify AS older
        USING "surf-discord".ticket_deadline_notify AS newer
        WHERE older.user_id = newer.user_id
          AND older.id < newer.id
        """.trimIndent()
    )

    transaction.exec(
        """
        CREATE UNIQUE INDEX IF NOT EXISTS ticket_deadline_notify_user_id_uq_idx
        ON "surf-discord".ticket_deadline_notify (user_id)
        """.trimIndent()
    )
}
