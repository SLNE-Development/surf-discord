package dev.slne.surf.discord.whitelist

internal data class WhitelistRoleChanges(
    val toAdd: Set<Long>,
    val toRemove: Set<Long>
)

internal fun calculateWhitelistRoleChanges(
    currentRoleHolderIds: Set<Long>,
    whitelistedMemberIds: Set<Long>
) = WhitelistRoleChanges(
    toAdd = whitelistedMemberIds - currentRoleHolderIds,
    toRemove = currentRoleHolderIds - whitelistedMemberIds
)
