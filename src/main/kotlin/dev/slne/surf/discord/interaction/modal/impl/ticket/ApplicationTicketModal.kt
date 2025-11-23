package dev.slne.surf.discord.interaction.modal.impl.ticket

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.getBean
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
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class ApplicationTicketModal(
    private val ticketService: TicketService
) : DiscordModal {
    override val id = "ticket:application"

    private val buttonRegistry by lazy {
        getBean<ButtonRegistry>()
    }

    private val selectMenuRegistry by lazy {
        getBean<SelectMenuRegistry>()
    }

    override fun create() = modal(id, "Bewerbung") {
        selectMenu(
            "Bewerbungstyp wählen...",
            selectMenuRegistry.get("ticket:application:select").create()
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
            lengthRange = 10..1024
        }

        textInput {
            id = "experience"
            label = "Hast du bereits Erfahrung in dem Bereich?"
            style = TextInputStyle.PARAGRAPH
            required = true
            lengthRange = 10..1024
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

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
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
                if (ticketService.hasOpenTicket(user.idLong, TicketType.APPLICATION)) {
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

        thread.sendMessage(user.asMention).queue()
        thread.sendMessageEmbeds(
            embed {
                title = translatable("${applicationType.name.formattedEnumEntryName} Bewerbung")
                description = translatable("ticket.support.application.embed.description")
                color = Colors.SUCCESS

                field {
                    name = translatable("ticket.support.application.embed.field.content")
                    value = content
                    inline = false
                }

                field {
                    name = translatable("ticket.support.application.embed.field.experience")
                    value = experience
                    inline = false
                }

                field {
                    name = translatable("ticket.support.application.embed.field.motivation")
                    value = motivation
                    inline = true
                }

                field {
                    name = translatable("ticket.support.application.embed.field.why")
                    value = why
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