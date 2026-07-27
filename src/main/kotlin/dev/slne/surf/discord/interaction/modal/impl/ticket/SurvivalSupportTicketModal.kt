package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.DiscordBot
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.replyError
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.section.Section
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

object SurvivalSupportTicketModal : DiscordModal {
    override val id = "ticket:support:survival"

    override fun create() = modal(id, translatable("ticket.support.survival.modal.title")) {
        textInput {
            id = "issue"
            label = translatable("ticket.support.survival.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 10..3737
            placeholder = translatable("ticket.support.survival.modal.field.issue.placeholder")
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val issue = interaction.getValue("issue")?.asString ?: return

        if (!DiscordBot.SURVIVAL_ENABLED && !event.member.hasPermission(DiscordPermission.TICKET_TYPE_BYPASS)) {
            interaction.replyEmbeds(embed {
                title = "Aktuell können keine Survival Support Tickets erstellt werden."
                description =
                    "Aufgrund der aktuellen Wartungsarbeiten am Survival Server können keine Survival Support Tickets erstellt werden."
                color = Colors.ERROR
            }).setEphemeral(true).queue()
            return
        }

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            TicketService.createTicket(
                interaction.hook,
                TicketType.SURVIVAL_SUPPORT,
                mapOf("issue" to issue)
            ) ?: run {
                if (TicketService.hasOpenTicket(user.idLong, TicketType.SURVIVAL_SUPPORT)) {
                    interaction.hook.editOriginal(translatable("ticket.support.survival.already_open"))
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

        thread.sendMessageComponents(
            Container.of(
                Section.of(
                    Thumbnail.fromUrl("https://castcrafter.de/favicon.png"),
                    TextDisplay.of(
                        translatable(
                            "ticket.support.survival.embed.title",
                            user.asMention
                        )
                    ),
                    TextDisplay.of(translatable("ticket.support.survival.embed.description"))
                ),
                Separator.createDivider(Separator.Spacing.LARGE),
                TextDisplay.of(translatable("ticket.support.survival.embed.field.issue")),
                TextDisplay.of(issue),
                Separator.createDivider(Separator.Spacing.LARGE),
                ActionRow.of(
                    ButtonRegistry.get("ticket:claim").button,
                    ButtonRegistry.get("ticket:close").button,
                    ButtonRegistry.get("whitelist:button:information").button
                ),
                TextDisplay.of("-# ${ticket.ticketId}"),
            )
        ).useComponentsV2().submit().thenAccept {
            thread.pinMessageById(it.idLong).queue()
        }

        interaction.hook.editOriginal(translatable("ticket.created", thread.asMention))
            .queue()
    }
}