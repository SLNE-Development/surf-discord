package dev.slne.discord.discord.interaction.select.menus

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.select.DiscordSelectMenu
import dev.slne.discord.message.translatable
import dev.slne.discord.ticket.TicketType
import dev.slne.discord.ticket.getTicketTypeByConfigName
import net.dv8tion.jda.api.components.selections.SelectOption
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
            SelectOption.of(ticketType.displayName, ticketType.configName)
                .withDescription(ticketType.description.run { substring(0, min(length, 100)) })
                .withEmoji(ticketType.emoji)

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
