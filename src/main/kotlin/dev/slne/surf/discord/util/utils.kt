package dev.slne.surf.discord.util

import kotlin.random.Random

val random = Random

fun Long.relativeDiscordTimeStamp() = "<t:$this:R>"
fun Long.absoluteDiscordTimeStamp() = "<t:$this:F>"