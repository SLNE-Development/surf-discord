package dev.slne.surf.discord.interaction.selectmenu.impl

import dev.slne.surf.discord.interaction.selectmenu.DiscordSelectMenu
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.springframework.stereotype.Component

@Component
class TicketTypeSelectMenu(
    private val ticketService: TicketService
) : DiscordSelectMenu {
    override val id = "ticket:type"
    override suspend fun create(hook: InteractionHook) = StringSelectMenu.create(id).apply {
        TicketType.entries.forEach {
            addOption(it.displayName, it.description, Emoji.fromUnicode(it.emoji))
        }

        setPlaceholder("Ticket Typ wählen...")
        setRequiredRange(1, 1)
    }.build()

    override suspend fun onSelect(event: StringSelectInteractionEvent) {
        val selected = event.selectedOptions.firstOrNull()
        if (selected == null) {
            event.hook.editOriginal("Ein Fehler ist aufgetreten. Bitte versuche es erneut.").queue()
            return
        }

        val ticketType = getTicketType(selected.label) ?: run {
            event.hook.editOriginal("Ein Fehler ist aufgetreten.").queue()
            return
        }

        if (ticketService.hasOpenTicket(event.user.idLong, ticketType)) {
            event.reply("Du hast bereits ein offenes Ticket dieses Typs.").setEphemeral(true)
                .queue()
            return
        }

        val modal = ticketType.modal

        if (modal != null) {
            event.hook.deleteOriginal().queue()
            event.replyModal(modal).queue()
        } else {
            event.hook.editOriginal("TODO: Ticket Modal: ${selected.label}").queue()
        }
    }

    private fun getTicketType(label: String) = TicketType.entries.find { it.displayName == label }
}