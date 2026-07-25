package dev.slne.surf.discord.config

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.whitelist.FreebuildWhitelistTable
import dev.slne.surf.discord.ticket.database.whitelist.WebAccountsTable
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
    fun `web accounts table references website owned table`() {
        assertEquals("account", WebAccountsTable.tableName)
    }

    @Test
    fun `schema setup contains only discord owned tables`() {
        val tables = discordOwnedTables.asList()

        assertContains(tables, TicketTable)
        assertFalse(WebAccountsTable in tables)
        assertFalse(FreebuildWhitelistTable in tables)
    }
}
