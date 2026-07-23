# Whitelist Schema Reference Design

## Context

`surf-discord` owns its ticket tables in the `surf-discord` PostgreSQL schema, but it only consumes:

- `surf-social.social_connections`
- `surf-whitelist.freebuild_whitelists`

The current `FreebuildWhitelistTable` definition incorrectly uses `schemedName("freebuild_whitelists")`, which resolves to `surf-discord.freebuild_whitelists`. `DatabaseConfiguration` also passes both external table objects to `SchemaUtils.create(...)`.

As a result, `surf-discord` creates and reads a second whitelist table in its own schema. Existing whitelist rows in `surf-whitelist.freebuild_whitelists` are invisible.

The failing interaction follows this path:

1. `WhitelistCreateButton.onClick()` calls `SocialService.isWhitelisted(discordId)`.
2. `SocialRepository.isWhitelisted(discordId)` finds the social connection but searches for its whitelist row in the wrong table and returns `false`.
3. `SurvivalWhitelistCreateModal.onSubmit()` eventually calls `SocialService.whitelist(...)`.
4. `SocialRepository.whitelist(...)` searches by Minecraft UUID and may attempt a new social-connection insert.
5. If that Discord user already owns a social-connection row, the insert violates `social_connections_new_discord_user_id_unique`.

The intended behavior for an existing Discord whitelist is already present in the interaction flow: treat it as already whitelisted and do not open or execute the creation flow. Reading the authoritative whitelist table restores that behavior; the existing social-connection row must not be replaced or duplicated.

## Design

Keep the existing Exposed table objects for cross-schema joins and references, but make their ownership explicit:

1. Map `FreebuildWhitelistTable` to `surf-whitelist.freebuild_whitelists`.
2. Keep `SocialConnectionsTable` mapped to `surf-social.social_connections`.
3. Maintain an explicit collection of tables owned by `surf-discord`.
4. Pass only that collection to `SchemaUtils.create(...)`.

The repository query and mutation logic remains unchanged. Once the mapping points at the authoritative whitelist table, existing whitelist rows are found by the current joins and guards.

## Data Safety

The application will not rename, copy, merge, or delete `surf-discord.freebuild_whitelists`. Removing or reconciling that mistakenly created table is an operator-controlled follow-up after its contents have been compared with the authoritative table.

`surf-discord` will also stop attempting to create or modify the externally owned social and whitelist tables.

## Testing

Add focused unit tests that verify:

- `FreebuildWhitelistTable.tableName` is `surf-whitelist.freebuild_whitelists`.
- the table collection passed to `SchemaUtils.create(...)` excludes both external table objects.
- the owned ticket tables remain present in that collection.

The regression tests do not require a live database because the failure is caused by static table ownership and qualification.

## Release

Bump the project version from `7.0.3` to `7.0.4`.
