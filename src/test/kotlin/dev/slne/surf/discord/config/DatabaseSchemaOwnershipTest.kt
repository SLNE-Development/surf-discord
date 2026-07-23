package dev.slne.surf.discord.config

import dev.slne.surf.discord.ticket.database.whitelist.FreebuildWhitelistTable
import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseSchemaOwnershipTest {
    @Test
    fun `whitelist table references authoritative schema`() {
        assertEquals(
            "surf-whitelist.freebuild_whitelists",
            FreebuildWhitelistTable.tableName
        )
    }
}
