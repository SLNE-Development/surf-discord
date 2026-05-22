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
class UnbanTicketModal(
    private val ticketService: TicketService,
    private val buttonRegistry: ButtonRegistry,
) : DiscordModal {
    override val id = "ticket:unban"

    override fun create() = modal(id, translatable("ticket.unban.modal.title")) {
        textInput {
            id = "punish-id"
            label = translatable("ticket.unban.modal.field.punish_id.label")
            style = TextInputStyle.SHORT
            placeholder = translatable("ticket.unban.modal.field.punish_id.placeholder")
            lengthRange = 1..20
            required = true
        }
        textInput {
            id = "issue"
            label = translatable("ticket.unban.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 10..200
            placeholder = translatable("ticket.unban.modal.field.issue.placeholder")
            required = true
        }

        textInput {
            id = "reason"
            label = translatable("ticket.unban.modal.field.reason.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 100..3000
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
                mapOf("issue" to issue, "reason" to reason, "punish-id" to punishId),
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

        thread.sendMessageComponents(
            Container.of(
                Section.of(
                    Thumbnail.fromUrl("https://castcrafter.de/favicon.png"),
                    TextDisplay.of(
                        translatable(
                            "ticket.unban.embed.title",
                            user.asMention
                        )
                    ),
                    TextDisplay.of(translatable("ticket.unban.embed.description"))
                ),
                Separator.createDivider(Separator.Spacing.LARGE),
                TextDisplay.of(translatable("ticket.unban.embed.field.punish_id")),
                TextDisplay.of(punishId),
                Separator.createDivider(Separator.Spacing.LARGE),
                TextDisplay.of(translatable("ticket.unban.embed.field.issue")),
                TextDisplay.of(issue),
                Separator.createDivider(Separator.Spacing.LARGE),
                TextDisplay.of(translatable("ticket.unban.embed.field.reason")),
                TextDisplay.of(reason),
                Separator.createDivider(Separator.Spacing.LARGE),
                ActionRow.of(
                    buttonRegistry.get("ticket:claim").button,
                    buttonRegistry.get("ticket:close").button,
                    buttonRegistry.get("whitelist:button:information").button
                ),
                TextDisplay.of("-# ${ticket.ticketId}"),
            )
        ).useComponentsV2().submit().thenAccept {
            thread.pinMessageById(it.idLong).queue()
        }
    }
}