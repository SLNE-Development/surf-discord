package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketData
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.replyError
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.springframework.stereotype.Component

@Component
class UnbanTicketModal(
    private val ticketService: TicketService,
    private val buttonRegistry: ButtonRegistry
) : DiscordModal {
    override val id = "ticket:unban"

    override fun create() = modal(id, translatable("ticket.unban.modal.title")) {
        field {
            id = "punish-id"
            label = translatable("ticket.unban.modal.field.punish_id.label")
            style = TextInputStyle.SHORT
            placeholder = translatable("ticket.unban.modal.field.punish_id.placeholder")
            required = true
        }
        field {
            id = "issue"
            label = translatable("ticket.unban.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 50..4000
            placeholder = translatable("ticket.unban.modal.field.issue.placeholder")
            required = true
        }

        field {
            id = "reason"
            label = translatable("ticket.unban.modal.field.reason.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 100..4000
            placeholder =
                translatable("ticket.unban.modal.field.reason.placeholder")
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val punishId = interaction.getValue("punish-id")?.asString ?: return
        val issue = interaction.getValue("issue")?.asString ?: return
        val reason = interaction.getValue("reason")?.asString ?: return

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
                interaction.hook,
                TicketType.UNBAN,
                TicketData.of("punish-id" to punishId, "reason" to reason, "issue" to issue)
            ) ?: run {
                if (ticketService.hasOpenTicket(user.idLong, TicketType.UNBAN)) {
                    interaction.hook.editOriginal(translatable("ticket.unban.already_open"))
                        .queue()
                } else {
                    interaction.hook.replyError()
                }
                return
            }

        val thread = ticket.getThreadChannel() ?: run {
            interaction.hook.replyError()
            return
        }

        interaction.hook.editOriginal(translatable("ticket.created", thread.asMention))
            .queue()

        thread.sendMessage(user.asMention).queue()
        thread.sendMessageEmbeds(
            embed {
                title = translatable("ticket.unban.embed.title")
                description = translatable("ticket.unban.embed.description")
                color = Colors.SUCCESS

                field {
                    name = translatable("ticket.unban.embed.field.punish_id")
                    value = punishId
                    inline = true
                }

                field {
                    name = translatable("ticket.unban.embed.field.issue")
                    value = issue
                    inline = true
                }

                field {
                    name = translatable("ticket.unban.embed.field.reason")
                    value = reason
                    inline = true
                }//TODO: Add Whitelist Information
            }
        ).addActionRow(
            buttonRegistry.get("ticket:close").button, //TODO: Add Laby.Net Profile Button
            buttonRegistry.get("ticket:claim").button
        ).queue()
    }
}