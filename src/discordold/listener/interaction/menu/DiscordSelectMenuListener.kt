package dev.slne.discordold.listener.interaction.menu

import dev.minn.jda.ktx.events.listener
import dev.slne.discordold.discord.interaction.select.DiscordSelectMenuManager
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import org.springframework.stereotype.Component

@Component
class DiscordSelectMenuListener(
    private val jda: JDA,
    private val discordSelectMenuManager: DiscordSelectMenuManager
) {

    @PostConstruct
    fun registerListener() {
        jda.listener<StringSelectInteractionEvent> { event ->
            val menu = discordSelectMenuManager.getMenu(event.componentId)

            if (menu == null) { // TODO: does not work - needed?
//                event.reply("Die Interaktion ist abgelaufen, oder konnte nicht gefunden werden!")
//                    .setEphemeral(true)
//                    .await()

                return@listener
            }

            event.values.mapNotNull { menu.getOptionByValue(it) }
                .let { menu.onSelect(event.interaction, it) }
        }
    }
}
