package dev.slne.surf.discord.interaction.selectmenu.impl

import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.interaction.selectmenu.DiscordSelectMenu
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.util.asTicketOrThrow
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.components.selections.SelectMenu
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

object TicketCloseReasonSelectMenu : DiscordSelectMenu {
    override val id = "ticket:close:reason"
    override suspend fun create(hook: InteractionHook): SelectMenu {
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
            event.hook.editOriginal(translatable("error")).queue()
            return
        }

        val ticket = TicketService.getTicketByThreadId(event.channel.idLong) ?: run {
            event.hook.editOriginal(translatable("error")).queue()
            return
        }

        if (selected.value == "custom") {
            event.replyModal(
                ModalRegistry.get("ticket:close:reason:custom").create()
            ).queue()
        } else {
            event.reply(translatable("ticket.closing")).setEphemeral(true).submit(true).await()

            TicketService.closeTicket(
                selected.value,
                event.hook
            )
        }
    }
}