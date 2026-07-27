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

object TwitchSupportTicketModal : DiscordModal {
    override val id = "ticket:support:twitch"

    override fun create() = modal(id, translatable("ticket.support.twitch.modal.title")) {
        textInput {
            id = "twitch-name"
            label = translatable("ticket.support.twitch.modal.field.name.label")
            style = TextInputStyle.SHORT
            lengthRange = 10..100
            placeholder = translatable("ticket.support.twitch.modal.field.name.placeholder")
            required = true
        }
        textInput {
            id = "issue"
            label = translatable("ticket.support.twitch.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 1..3500
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
            TicketService.createTicket(
                interaction.hook,
                TicketType.TWITCH_SUPPORT,
                mapOf("issue" to issue, "twitchName" to twitchName)
            ) ?: run {
                if (TicketService.hasOpenTicket(user.idLong, TicketType.TWITCH_SUPPORT)) {
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

        thread.sendMessageComponents(
            Container.of(
                Section.of(
                    Thumbnail.fromUrl("https://castcrafter.de/favicon.png"),
                    TextDisplay.of(
                        translatable(
                            "ticket.support.twitch.embed.title",
                            user.asMention
                        )
                    ),
                    TextDisplay.of(translatable("ticket.support.twitch.embed.description"))
                ),
                Separator.createDivider(Separator.Spacing.LARGE),
                TextDisplay.of(translatable("ticket.support.twitch.embed.field.twitch_name")),
                TextDisplay.of(twitchName),
                Separator.createDivider(Separator.Spacing.LARGE),
                TextDisplay.of(translatable("ticket.support.twitch.embed.field.issue")),
                TextDisplay.of(issue),
                Separator.createDivider(Separator.Spacing.LARGE),
                ActionRow.of(
                    ButtonRegistry.get("ticket:claim").button,
                    ButtonRegistry.get("ticket:close").button
                ),
                TextDisplay.of("-# ${ticket.ticketId}"),
            )
        ).useComponentsV2().submit().thenAccept {
            thread.pinMessageById(it.idLong).queue()
        }
    }
}