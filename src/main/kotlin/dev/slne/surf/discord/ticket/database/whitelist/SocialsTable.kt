package dev.slne.surf.discord.ticket.database.whitelist

import org.jetbrains.exposed.v1.core.Table
import java.util.*

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
