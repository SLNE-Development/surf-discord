package dev.slne.discord.persistence

import jakarta.persistence.Persistence

private val persistenceManager = Persistence.createEntityManagerFactory("discord")

