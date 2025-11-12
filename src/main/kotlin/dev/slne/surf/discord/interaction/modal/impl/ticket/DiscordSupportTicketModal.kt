package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.replyError
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class DiscordSupportTicketModal(
    private val ticketService: TicketService,
) : DiscordModal {
    private val buttonRegistry by lazy {
        getBean<ButtonRegistry>()
    }
    override val id = "ticket:support:discord"

    override fun create() = modal(id, translatable("ticket.support.discord.modal.title")) {
        textInput {
            id = "issue"
            label = translatable("ticket.support.discord.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 10..4000
            placeholder = translatable("ticket.support.discord.modal.field.issue.placeholder")
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val issue = interaction.getValue("issue")?.asString ?: return

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
                interaction.hook,
                TicketType.DISCORD_SUPPORT,
                mapOf("issue" to issue)
            ) ?: run {
                if (ticketService.hasOpenTicket(user.idLong, TicketType.DISCORD_SUPPORT)) {
                    interaction.hook.editOriginal(translatable("ticket.support.discord.already_open"))
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
                title = translatable("ticket.support.discord.embed.title")
                description = translatable("ticket.support.discord.embed.description")
                color = Colors.SUCCESS

                field {
                    name = translatable("ticket.support.discord.embed.field.issue")
                    value = issue
                    inline = true
                }
            }
        ).addComponents(
            ActionRow.of(
                buttonRegistry.get("ticket:close").button,
                buttonRegistry.get("ticket:claim").button
            )
        ).submit(true).thenAccept {
            thread.pinMessageById(it.idLong).queue()
        }
    }
}