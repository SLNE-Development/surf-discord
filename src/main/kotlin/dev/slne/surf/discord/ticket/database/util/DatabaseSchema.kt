package dev.slne.surf.discord.ticket.database.util

private const val DATABASE_SCHEMA = "surf-discord"

fun schemedName(name: String): String = "$DATABASE_SCHEMA.$name"
