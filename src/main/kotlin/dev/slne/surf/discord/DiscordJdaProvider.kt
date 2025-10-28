package dev.slne.surf.discord

import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
object DiscordJdaProvider {
    lateinit var jda: JDA
        private set

    @Autowired
    fun init(jda: JDA) {
        DiscordJdaProvider.jda = jda
    }
}

val jda get() = DiscordJdaProvider.jda