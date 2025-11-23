package dev.slne.surf.discord.interaction.selectmenu.impl

import dev.slne.surf.discord.interaction.selectmenu.DiscordSelectMenu
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import org.springframework.stereotype.Component

@Component
class TicketTypeSelectMenu(
    private val ticketService: TicketService
) : DiscordSelectMenu {
    override val id = "ticket:type"
    override suspend fun create(hook: InteractionHook) =
        StringSelectMenu.create(id).apply {
            TicketType.entries.forEach {
                addOption(it.displayName, it.description, Emoji.fromUnicode(it.emoji))
            }

            setPlaceholder(translatable("ticket.type.select.placeholder"))
            setRequiredRange(1, 1)
        }.build()

    override suspend fun onSelect(event: StringSelectInteractionEvent) {
        val selected = event.selectedOptions.firstOrNull()
        if (selected == null) {
            event.hook.editOriginal(translatable("error")).queue()
            return
        }

        val ticketType = getTicketType(selected.label) ?: run {
            event.hook.editOriginal(translatable("error")).queue()
            return
        }

        if (ticketService.hasOpenTicket(event.user.idLong, ticketType)) {
            event.reply(translatable("ticket.already-open")).setEphemeral(true)
                .queue()
            return
        }

        val modal = ticketType.modal

        if (modal != null) {
            event.hook.deleteOriginal().queue()
            event.replyModal(modal).queue()
        } else {
            event.hook.editOriginal(translatable("error")).queue()
        }
    }

    private fun getTicketType(label: String) = TicketType.entries.find { it.displayName == label }
}