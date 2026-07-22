package dev.slne.surf.discord.ticket.database.util

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.core.vendors.currentDialect
import org.jetbrains.exposed.v1.r2dbc.transactions.TransactionManager

internal object PostgreSQLUpdateHook {

    private const val UPDATED_AT_FUNCTION =
        "\"surf-discord\".surf_set_updated_at"

    fun modifyStatements(
        statements: List<String>,
        table: Table,
        updatedAt: Column<*>,
        tableName: String
    ): List<String> {
        if (currentDialect !is PostgreSQLDialect) {
            return statements
        }

        val transaction = TransactionManager.current()
        val tableIdentity = transaction.identity(table)
        val updatedAtIdentity = transaction.identity(updatedAt)

        val rawTriggerName =
            "${tableName.substringAfterLast('.')}_set_updated_at"

        val triggerName =
            transaction.db.identifierManager
                .cutIfNecessaryAndQuote(rawTriggerName)

        val createFunction = """
            CREATE OR REPLACE FUNCTION $UPDATED_AT_FUNCTION()
            RETURNS trigger AS $$
            BEGIN
                NEW.$updatedAtIdentity :=
                    CURRENT_TIMESTAMP AT TIME ZONE 'UTC';
                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql
        """.trimIndent()

        val dropTrigger =
            "DROP TRIGGER IF EXISTS $triggerName ON $tableIdentity"

        val createTrigger =
            "CREATE TRIGGER $triggerName " +
                    "BEFORE UPDATE ON $tableIdentity " +
                    "FOR EACH ROW " +
                    "EXECUTE FUNCTION $UPDATED_AT_FUNCTION()"

        return statements + createFunction + dropTrigger + createTrigger
    }
}