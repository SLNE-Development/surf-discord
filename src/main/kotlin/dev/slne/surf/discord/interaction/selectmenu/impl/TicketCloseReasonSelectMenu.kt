package dev.slne.surf.discord.interaction.selectmenu.impl

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.interaction.selectmenu.DiscordSelectMenu
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.util.asTicketOrThrow
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.springframework.stereotype.Component

@Component
class TicketCloseReasonSelectMenu(
    private val ticketService: TicketService
) : DiscordSelectMenu {
    override val id = "ticket:close:reason"
    override suspend fun create(hook: InteractionHook): StringSelectMenu {
        val ticket = hook.asTicketOrThrow()

        return StringSelectMenu.create(id).apply {
            addOption("Eigener Grund", "custom")

            ticket.ticketType.closeReasons.forEach { rsn ->
                addOption(rsn.displayName, rsn.description)
            }
        }.build()
    }

    override suspend fun onSelect(event: StringSelectInteractionEvent) {
        val selected = event.selectedOptions.firstOrNull()
        if (selected == null) {
            event.hook.editOriginal("Ein Fehler ist aufgetreten. Bitte versuche es erneut.").queue()
            return
        }

        val ticket = ticketService.getTicketByThreadId(event.channel.idLong) ?: run {
            event.hook.editOriginal("Ein Fehler ist aufgetreten.").queue()
            return
        }

        if (selected.value == "custom") {
            event.replyModal(
                getBean<ModalRegistry>().get("ticket:close:reason:custom").create()
            ).queue()
        } else {
            event.reply("Das Ticket wird geschlossen...").setEphemeral(true).queue()

            ticketService.closeTicket(
                ticket,
                selected.value,
                event.user
            )

            event.hook.deleteOriginal().queue()
        }
    }
}