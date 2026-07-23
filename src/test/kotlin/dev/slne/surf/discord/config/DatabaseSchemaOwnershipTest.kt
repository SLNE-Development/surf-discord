package dev.slne.surf.discord.config

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.whitelist.FreebuildWhitelistTable
import dev.slne.surf.discord.ticket.database.whitelist.SocialConnectionsTable
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DatabaseSchemaOwnershipTest {
    @Test
    fun `whitelist table references authoritative schema`() {
        assertEquals(
            "surf-whitelist.freebuild_whitelists",
            FreebuildWhitelistTable.tableName
        )
    }

    @Test
    fun `schema setup contains only discord owned tables`() {
        val tables = discordOwnedTables.asList()

        assertContains(tables, TicketTable)
        assertFalse(SocialConnectionsTable in tables)
        assertFalse(FreebuildWhitelistTable in tables)
    }
}
