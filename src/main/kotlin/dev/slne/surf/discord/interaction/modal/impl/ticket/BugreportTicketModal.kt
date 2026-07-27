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

object BugreportTicketModal : DiscordModal {
    override val id = "ticket:bugreport"

    override fun create() = modal(id, translatable("ticket.bugreport.modal.title")) {
        textInput {
            id = "issue"
            label = translatable("ticket.bugreport.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 10..3000
            placeholder = translatable("ticket.bugreport.modal.field.issue.placeholder")
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val issue = interaction.getValue("issue")?.asString ?: return

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            TicketService.createTicket(
                interaction.hook,
                TicketType.BUGREPORT,
                mapOf("issue" to issue)
            ) ?: run {
                if (TicketService.hasOpenTicket(user.idLong, TicketType.BUGREPORT)) {
                    interaction.hook.editOriginal(translatable("ticket.bugreport.already_open"))
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

        val components = buildList {
            add(
                Section.of(
                    Thumbnail.fromUrl("https://castcrafter.de/favicon.png"),
                    TextDisplay.of(
                        translatable("ticket.bugreport.embed.title", user.asMention)
                    ),
                    TextDisplay.of(
                        translatable("ticket.bugreport.embed.description")
                    )
                )
            )

            add(Separator.createDivider(Separator.Spacing.LARGE))

            add(TextDisplay.of(translatable("ticket.bugreport.embed.field.issue")))
            add(TextDisplay.of(issue))
            add(Separator.createDivider(Separator.Spacing.LARGE))
            add(
                ActionRow.of(
                    ButtonRegistry.get("ticket:claim").button,
                    ButtonRegistry.get("ticket:close").button,
                    ButtonRegistry.get("whitelist:button:information").button
                )
            )
        }

        thread.sendMessageComponents(
            Container.of(components)
        ).useComponentsV2().submit().thenAccept {
            thread.pinMessageById(it.idLong).queue()
        }
    }
}