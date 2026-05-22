package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.replyError
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.section.Section
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class ComplaintTicketModal(
    private val ticketService: TicketService,
    private val buttonRegistry: ButtonRegistry,
) : DiscordModal {
    override val id = "ticket:complaint"

    override fun create() = modal(id, translatable("ticket.complaint.modal.title")) {
        textInput {
            id = "target"
            label = translatable("ticket.complaint.modal.field.target.label")
            style = TextInputStyle.SHORT
            placeholder = translatable("ticket.complaint.modal.field.target.placeholder")
            required = false
            lengthRange = 3..16
        }
        textInput {
            id = "issue"
            label = translatable("ticket.complaint.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 10..3000
            placeholder = translatable("ticket.complaint.modal.field.issue.placeholder")
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val target = interaction.getValue("target")?.asString ?: return
        val issue = interaction.getValue("issue")?.asString ?: return

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
                interaction.hook,
                TicketType.COMPLAINT,
                mapOf("issue" to issue, "target" to target),
            ) ?: run {
                if (ticketService.hasOpenTicket(user.idLong, TicketType.COMPLAINT)) {
                    interaction.hook.editOriginal(translatable("ticket.complaint.already_open"))
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

        thread.sendMessageComponents(
            Container.of(
                Section.of(
                    Thumbnail.fromUrl("https://castcrafter.de/favicon.png"),
                    TextDisplay.of(
                        translatable(
                            "ticket.complaint.embed.title",
                            user.asMention
                        )
                    ),
                    TextDisplay.of(translatable("ticket.complaint.embed.description"))
                ),
                Separator.createDivider(Separator.Spacing.LARGE),
                TextDisplay.of(translatable("ticket.complaint.embed.field.target")),
                TextDisplay.of(target),
                Separator.createDivider(Separator.Spacing.LARGE),
                TextDisplay.of(translatable("ticket.complaint.embed.field.issue")),
                TextDisplay.of(issue),
                Separator.createDivider(Separator.Spacing.LARGE),
                ActionRow.of(
                    buttonRegistry.get("ticket:claim").button,
                    buttonRegistry.get("ticket:close").button
                ),
                TextDisplay.of("-# ${ticket.ticketId}"),
            )
        ).useComponentsV2().submit().thenAccept {
            thread.pinMessageById(it.idLong).queue()
        }
    }
}