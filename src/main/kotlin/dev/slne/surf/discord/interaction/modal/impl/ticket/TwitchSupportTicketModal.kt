package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
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
class TwitchSupportTicketModal(
    private val ticketService: TicketService,
    private val buttonRegistry: ButtonRegistry,
) : DiscordModal {
    override val id = "ticket:support:twitch"

    override fun create() = modal(id, translatable("ticket.support.twitch.modal.title")) {
        textInput {
            id = "twitch-name"
            label = translatable("ticket.support.twitch.modal.field.name.label")
            style = TextInputStyle.SHORT
            lengthRange = 10..4000
            placeholder = translatable("ticket.support.twitch.modal.field.name.placeholder")
            required = true
        }
        textInput {
            id = "issue"
            label = translatable("ticket.support.twitch.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 1..4000
            placeholder = translatable("ticket.support.twitch.modal.field.issue.placeholder")
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val issue = interaction.getValue("issue")?.asString ?: return
        val twitchName = interaction.getValue("twitch-name")?.asString ?: return

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
                interaction.hook,
                TicketType.TWITCH_SUPPORT,
                mapOf("issue" to issue, "twitchName" to twitchName)
            ) ?: run {
                if (ticketService.hasOpenTicket(user.idLong, TicketType.TWITCH_SUPPORT)) {
                    interaction.hook.editOriginal(translatable("ticket.support.twitch.already_open"))
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
                title = translatable("ticket.support.twitch.embed.title")
                description = translatable("ticket.support.twitch.embed.description")
                color = Colors.SUCCESS

                field {
                    name = translatable("ticket.support.twitch.embed.field.twitch_name")
                    value = twitchName
                    inline = true
                }

                issue.chunked(1024).forEach { chunk ->
                    field {
                        name = translatable("ticket.support.twitch.embed.field.issue")
                        value = chunk
                        inline = true
                    }
                }

            }
        ).addComponents(
            ActionRow.of(
                buttonRegistry.get("ticket:claim").button,
                buttonRegistry.get("ticket:close").button
            )
        ).submit(true).thenAccept {
            thread.pinMessageById(it.idLong).queue()
        }
    }
}