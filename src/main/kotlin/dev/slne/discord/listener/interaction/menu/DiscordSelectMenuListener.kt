package dev.slne.discord.listener.interaction.menu

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.discord.interaction.select.DiscordSelectMenuManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class DiscordSelectMenuListener(jda: JDA) {

    init {
        jda.listener<StringSelectInteractionEvent> { event ->
            val menu = DiscordSelectMenuManager.getMenu(event.componentId)

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
