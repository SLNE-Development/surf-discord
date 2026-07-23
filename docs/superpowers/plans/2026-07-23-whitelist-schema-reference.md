# Whitelist Schema Reference Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make `surf-discord` read the authoritative whitelist table from `surf-whitelist` and stop creating externally owned social/whitelist tables.

**Architecture:** Keep Exposed table objects for cross-schema joins, but qualify each external table with its owner schema. Centralize the tables owned by `surf-discord` in one internal collection and pass only that collection to `SchemaUtils.create(...)`.

**Tech Stack:** Kotlin 2.3.21, Exposed R2DBC 1.3.0, Kotlin Test, Gradle 9.6.1

## Global Constraints

- Target branch: `version/7.0.0`.
- Preserve `surf-social.social_connections` as an external reference.
- Use `surf-whitelist.freebuild_whitelists` as the authoritative whitelist table.
- Do not delete, migrate, or merge `surf-discord.freebuild_whitelists`.
- Do not change the Java native-access warning.
- Bump the version from `7.0.3` to `7.0.4`.

---

### Task 1: Reference the Authoritative Whitelist Table

**Files:**
- Modify: `build.gradle.kts`
- Modify: `src/main/kotlin/dev/slne/surf/discord/ticket/database/whitelist/SocialsTable.kt`
- Create: `src/test/kotlin/dev/slne/surf/discord/config/DatabaseSchemaOwnershipTest.kt`

**Interfaces:**
- Consumes: `FreebuildWhitelistTable.tableName`
- Produces: `FreebuildWhitelistTable` mapped to `surf-whitelist.freebuild_whitelists`

- [ ] **Step 1: Enable Kotlin tests**

Add this line inside the existing `dependencies` block:

```kotlin
testImplementation(kotlin("test"))
```

Add this top-level task configuration after the `dependencies` block:

```kotlin
tasks.test {
    useJUnitPlatform()
}
```

- [ ] **Step 2: Write the failing table-qualification test**

```kotlin
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
```

- [ ] **Step 3: Run the test and verify RED**

Run:

```powershell
.\gradlew.bat test --tests dev.slne.surf.discord.config.DatabaseSchemaOwnershipTest
```

Expected: FAIL because the actual table name is `surf-discord.freebuild_whitelists`.

- [ ] **Step 4: Correct the whitelist table name**

Change `FreebuildWhitelistTable` to:

```kotlin
object FreebuildWhitelistTable :
    AuditableLongIdTable("surf-whitelist.freebuild_whitelists") {
    val socialConnectionId =
        reference("social_connection_id", SocialConnectionsTable).uniqueIndex()
    val blocked = bool("blocked").default(false)
}
```

Remove the now-unused `schemedName` import.

- [ ] **Step 5: Run the test and verify GREEN**

Run:

```powershell
.\gradlew.bat test --tests dev.slne.surf.discord.config.DatabaseSchemaOwnershipTest
```

Expected: PASS with one test.

- [ ] **Step 6: Commit**

```powershell
git add -- build.gradle.kts src/main/kotlin/dev/slne/surf/discord/ticket/database/whitelist/SocialsTable.kt src/test/kotlin/dev/slne/surf/discord/config/DatabaseSchemaOwnershipTest.kt
git commit -m "fix(database): reference authoritative whitelist schema"
```

### Task 2: Create Only Discord-Owned Tables

**Files:**
- Modify: `src/main/kotlin/dev/slne/surf/discord/config/DatabaseConfig.kt`
- Modify: `src/test/kotlin/dev/slne/surf/discord/config/DatabaseSchemaOwnershipTest.kt`

**Interfaces:**
- Consumes: all existing `surf-discord` ticket table objects
- Produces: `internal val discordOwnedTables: Array<Table>`

- [ ] **Step 1: Extend the test with the ownership contract**

Add imports:

```kotlin
import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.whitelist.SocialConnectionsTable
import kotlin.test.assertContains
import kotlin.test.assertFalse
```

Add the test:

```kotlin
@Test
fun `schema setup contains only discord owned tables`() {
    val tables = discordOwnedTables.asList()

    assertContains(tables, TicketTable)
    assertFalse(SocialConnectionsTable in tables)
    assertFalse(FreebuildWhitelistTable in tables)
}
```

- [ ] **Step 2: Run the test and verify RED**

Run:

```powershell
.\gradlew.bat test --tests dev.slne.surf.discord.config.DatabaseSchemaOwnershipTest
```

Expected: test compilation FAIL because `discordOwnedTables` does not exist yet.

- [ ] **Step 3: Define the owned table collection**

In `DatabaseConfig.kt`, import `org.jetbrains.exposed.v1.core.Table` and add:

```kotlin
internal val discordOwnedTables = arrayOf<Table>(
    TicketTable,
    TicketMemberTable,
    TicketDataTable,
    TicketStaffTable,
    TicketMessagesTable,
    TicketAttachmentsTable,
    ReplyDeadlineTable,
    DeadlineNotifyTable
)
```

Replace the existing `SchemaUtils.create(...)` call with:

```kotlin
SchemaUtils.create(*discordOwnedTables)
```

Remove imports for `SocialConnectionsTable` and `FreebuildWhitelistTable` from `DatabaseConfig.kt`.

- [ ] **Step 4: Run the test and verify GREEN**

Run:

```powershell
.\gradlew.bat test --tests dev.slne.surf.discord.config.DatabaseSchemaOwnershipTest
```

Expected: PASS with two tests.

- [ ] **Step 5: Commit**

```powershell
git add -- src/main/kotlin/dev/slne/surf/discord/config/DatabaseConfig.kt src/test/kotlin/dev/slne/surf/discord/config/DatabaseSchemaOwnershipTest.kt
git commit -m "fix(database): create only discord-owned tables"
```

### Task 3: Release and Verification

**Files:**
- Modify: `gradle.properties`

**Interfaces:**
- Consumes: verified schema ownership changes
- Produces: release version `7.0.4`

- [ ] **Step 1: Bump the version**

```properties
version=7.0.4
```

- [ ] **Step 2: Commit**

```powershell
git add -- gradle.properties
git commit -m "chore: bump version to 7.0.4"
```

- [ ] **Step 3: Run focused tests**

Run:

```powershell
.\gradlew.bat test --tests dev.slne.surf.discord.config.DatabaseSchemaOwnershipTest --no-daemon
```

Expected: PASS with two tests.

- [ ] **Step 4: Run the full build**

Run:

```powershell
.\gradlew.bat clean build --no-daemon
```

Expected: BUILD SUCCESSFUL with zero failed tests.

- [ ] **Step 5: Check the diff**

Run:

```powershell
git diff --check origin/version/7.0.0...HEAD
git status -sb
```

Expected: no whitespace errors and only intended files changed.
