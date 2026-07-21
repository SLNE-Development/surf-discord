package dev.slne.surf.discord.ticket.database.util

import org.jetbrains.exposed.v1.core.Schema

private const val DATABASE_SCHEMA = "surf-discord"
val DiscordSchema = Schema(DATABASE_SCHEMA)

fun schemedName(name: String): String = "$DATABASE_SCHEMA.$name"
