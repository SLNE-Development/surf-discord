package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.DiscordBot
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.interaction.selectmenu.SelectMenuRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.TicketApplicationType
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.formattedEnumEntryName
import dev.slne.surf.discord.util.replyError
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.section.Section
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

object ApplicationTicketModal : DiscordModal {
    override val id = "ticket:application"

    override fun create() = modal(id, "Bewerbung") {
        selectMenu(
            "Bewerbungstyp wählen...",
            SelectMenuRegistry.get("ticket:application:select").create()
        )

        textInput {
            id = "content"
            label = "Deine Bewerbung"
            style = TextInputStyle.PARAGRAPH
            required = true
            lengthRange = 100..1024
        }

        textInput {
            id = "motivation"
            label = "Warum sollten wir dich auswählen?"
            style = TextInputStyle.PARAGRAPH
            required = true
            lengthRange = 10..1024
        }

        textInput {
            id = "why"
            label = "Warum bewirbst du dich gerade bei uns?"
            style = TextInputStyle.PARAGRAPH
            required = true
            lengthRange = 10..500
        }

        textInput {
            id = "experience"
            label = "Hast du bereits Erfahrung in dem Bereich?"
            style = TextInputStyle.PARAGRAPH
            required = true
            lengthRange = 10..750
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val applicationType = TicketApplicationType.valueOf(
            interaction.getValue("ticket:application:select")?.asStringList?.first() ?: return
        )
        val content = interaction.getValue("content")?.asString ?: return
        val motivation = interaction.getValue("motivation")?.asString ?: return
        val why = interaction.getValue("why")?.asString ?: return
        val experience = interaction.getValue("experience")?.asString ?: return

        if ((!DiscordBot.SUPPORT_APPLICATION_ENABLED && applicationType == TicketApplicationType.SUPPORTER) || (!DiscordBot.TWITCH_APPLICATION_ENABLED && applicationType == TicketApplicationType.TWITCH_MODERATOR)) {
            interaction.replyEmbeds(embed {
                title = translatable("ticket.application.not-available.title")
                description = translatable("ticket.application.not-available.description")
                color = Colors.WARNING
            }).setEphemeral(true).queue()
            return
        }

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            TicketService.createTicket(
                interaction.hook,
                TicketType.APPLICATION,
                mapOf(
                    "application_type" to applicationType.name,
                    "content" to content,
                    "motivation" to motivation,
                    "why" to why,
                    "experience" to experience
                )
            ) ?: run {
                if (TicketService.hasOpenTicket(user.idLong, TicketType.APPLICATION)) {
                    interaction.hook.editOriginal(translatable("ticket.support.application.already_open"))
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
                            "## ${applicationType.name.formattedEnumEntryName} Bewerbung"
                        )
                    ),
                    TextDisplay.of(
                        translatable("ticket.support.application.embed.description")
                    )
                ),

                Separator.createDivider(Separator.Spacing.LARGE),

                TextDisplay.of(
                    translatable("ticket.support.application.embed.field.content")
                ),
                TextDisplay.of(content),

                Separator.createDivider(Separator.Spacing.LARGE),

                TextDisplay.of(translatable("ticket.support.application.embed.field.experience")),
                TextDisplay.of(experience),

                Separator.createDivider(Separator.Spacing.LARGE),

                TextDisplay.of(translatable("ticket.support.application.embed.field.motivation")),
                TextDisplay.of(motivation),

                Separator.createDivider(Separator.Spacing.LARGE),

                TextDisplay.of(translatable("ticket.support.application.embed.field.why")),
                TextDisplay.of(why),

                Separator.createDivider(Separator.Spacing.LARGE),

                ActionRow.of(
                    ButtonRegistry.get("ticket:claim").button,
                    ButtonRegistry.get("ticket:close").button
                )
            )
        ).useComponentsV2().submit().thenAccept {
            thread.pinMessageById(it.idLong).queue()
        }
    }
}