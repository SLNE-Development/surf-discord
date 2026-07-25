package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.ticket.database.util.AuditableLongIdTable
import org.jetbrains.exposed.v1.core.Table
import java.util.*

object FreebuildWhitelistTable : AuditableLongIdTable("surf-whitelist.freebuild_whitelists") {
    val userId = reference("user_id", WebUsersTable.id)
    val blocked = bool("blocked").default(false)
}

object WebUsersTable : Table("user") {
    val id = text("id").transform(
        wrap = { UUID.fromString(it) },
        unwrap = UUID::toString
    )

    override val primaryKey = PrimaryKey(id)
}

object WebAccountsTable : Table("account") {
    val userId = reference("userId", WebUsersTable.id)
    val provider = text("provider")
    val providerAccountId = text("providerAccountId")

    override val primaryKey = PrimaryKey(provider, providerAccountId)
}

object WebAccountProviders {
    const val MINECRAFT = "minecraft"
    const val DISCORD = "discord"
}
