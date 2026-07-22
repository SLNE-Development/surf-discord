package dev.slne.surf.discord.ticket.database.util

import dev.slne.surf.discord.ticket.database.column.CurrentOffsetDateTime
import dev.slne.surf.discord.ticket.database.column.offsetDateTime
import org.jetbrains.exposed.v1.core.dao.id.ULongIdTable

open class AuditableLongIdTable(name: String) : ULongIdTable(name) {

    val createdAt = offsetDateTime("created_at")
        .defaultExpression(CurrentOffsetDateTime())

    val updatedAt = offsetDateTime("updated_at")
        .defaultExpression(CurrentOffsetDateTime(includeUpdate = true))

    override fun createStatement(): List<String> {
        val statements = super.createStatement()

        return PostgreSQLUpdateHook.modifyStatements(
            statements = statements,
            table = this,
            updatedAt = updatedAt,
            tableName = tableName
        )
    }
}