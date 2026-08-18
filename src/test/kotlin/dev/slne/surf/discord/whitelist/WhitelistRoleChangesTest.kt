package dev.slne.surf.discord.whitelist

import kotlin.test.Test
import kotlin.test.assertEquals

class WhitelistRoleChangesTest {
    @Test
    fun `calculates missing and obsolete whitelist roles`() {
        val changes = calculateWhitelistRoleChanges(
            currentRoleHolderIds = setOf(1L, 2L),
            whitelistedMemberIds = setOf(2L, 3L)
        )

        assertEquals(setOf(3L), changes.toAdd)
        assertEquals(setOf(1L), changes.toRemove)
    }

    @Test
    fun `returns no changes when whitelist role is in sync`() {
        val changes = calculateWhitelistRoleChanges(
            currentRoleHolderIds = setOf(1L, 2L),
            whitelistedMemberIds = setOf(1L, 2L)
        )

        assertEquals(emptySet(), changes.toAdd)
        assertEquals(emptySet(), changes.toRemove)
    }
}
