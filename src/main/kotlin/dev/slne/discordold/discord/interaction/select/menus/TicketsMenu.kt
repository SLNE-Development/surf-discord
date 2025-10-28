package dev.slne.discordold.discord.interaction.select.menus

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.SelectOption
import dev.slne.discordold.discord.interaction.modal.DiscordModalManager
import dev.slne.discordold.discord.interaction.select.DiscordSelectMenu
import dev.slne.discordold.message.translatable
import dev.slne.discordold.ticket.TicketType
import dev.slne.discordold.ticket.getTicketTypeByConfigName
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import kotlin.math.min

class TicketsMenu(
    idSuffix: String,
    private val discordModalManager: DiscordModalManager
) : DiscordSelectMenu(
    "menu:tickets-$idSuffix",
    translatable("menu.ticket.select.placeholder"),
    TicketType.entries.filter { it != TicketType.WHITELIST && it != TicketType.SURVIVAL_SUPPORT }
        .map { ticketType ->
            SelectOption(
                ticketType.displayName,
                ticketType.configName,
                ticketType.description.run { substring(0, min(length, 100)) },
                ticketType.emoji
            )
        },
    1..1
) {
    override suspend fun onSelect(
        interaction: StringSelectInteraction,
        selectedOptions: List<SelectOption>
    ) {
        val ticketType = getTicketTypeByConfigName(selectedOptions.first().value)

        if (ticketType == null) {
            interaction.reply(translatable("error.generic")).setEphemeral(true).await()
            return
        }

        val guild = interaction.guild

        if (guild == null) { // Should generally never happen
            interaction.reply(translatable("error.generic")).setEphemeral(true).await()
            return
        }

        discordModalManager.createByTicketType(ticketType)
            .startChannelCreation(interaction, guild)
    }
}
