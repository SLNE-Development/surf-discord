package dev.slne.surf.discord.interaction.selectmenu.impl.application

import dev.slne.surf.discord.interaction.selectmenu.DiscordSelectMenu
import dev.slne.surf.discord.ticket.TicketApplicationType
import dev.slne.surf.discord.util.formattedEnumEntryName
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import org.springframework.stereotype.Component

@Component
class ApplicationTypeSelectMenu : DiscordSelectMenu {
    override val id = "ticket:application:select"

    override fun create() = StringSelectMenu
        .create(id)
        .addOptions(TicketApplicationType.entries.map {
            SelectOption.of(
                it.name.formattedEnumEntryName,
                it.name
            )
        })
        .build()
}