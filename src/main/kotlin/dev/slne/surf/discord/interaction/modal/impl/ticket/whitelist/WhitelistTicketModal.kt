package dev.slne.surf.discord.interaction.modal.impl.ticket.whitelist

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
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class WhitelistTicketModal(
    private val ticketService: TicketService,
) : DiscordModal {
    private val buttonRegistry by lazy {
        getBean<ButtonRegistry>()
    }
    override val id = "ticket:whitelist"
    override fun create() = modal(id, translatable("ticket.whitelist.title")) {
        textInput {
            id = "whitelist-name"
            label = translatable("ticket.whitelist.field.name")
            style = TextInputStyle.SHORT
            placeholder = "CastCrafter"
            required = true
            lengthRange = 3..16
        }

        textInput {
            id = "whitelist-twitch"
            label = translatable("ticket.whitelist.field.twitch")
            style = TextInputStyle.SHORT
            placeholder = "CastCrafter"
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val whitelistName = interaction.getValue("whitelist-name")?.asString ?: return
        val whitelistTwitch = interaction.getValue("whitelist-twitch")?.asString ?: return

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket = ticketService.createTicket(
            interaction.hook,
            TicketType.WHITELIST,
            mapOf("minecraft" to whitelistName, "twitch" to whitelistTwitch),
        ) ?: run {
            if (ticketService.hasOpenTicket(user.idLong, TicketType.WHITELIST)) {
                interaction.hook.editOriginal(translatable("ticket.whitelist.already_open"))
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

        interaction.hook.editOriginal(translatable("ticket.opened", thread.asMention))
            .queue()

        thread.sendMessage(user.asMention).queue()
        thread.sendMessageEmbeds(
            embed {
                title = translatable("ticket.whitelist.embed.title")
                description = translatable("ticket.whitelist.embed.description")
                color = Colors.SUCCESS

                field {
                    name = translatable("ticket.whitelist.embed.field.name")
                    value = whitelistName
                    inline = true
                }

                field {
                    name = translatable("ticket.whitelist.embed.field.twitch")
                    value = whitelistTwitch
                    inline = true
                }

                field {
                    name = translatable("ticket.whitelist.embed.field.discord_name")
                    value = interaction.user.name
                    inline = true
                }

                field {
                    name = translatable("ticket.whitelist.embed.field.discord_id")
                    value = interaction.user.id
                    inline = true
                }
            }
        ).addComponents(
            ActionRow.of(
                buttonRegistry.get("ticket:whitelist:complete").button,
                buttonRegistry.get("ticket:close").button,
                buttonRegistry.get("ticket:claim").button
            ), ActionRow.of(
                Button.link("https://twitch.tv/$whitelistTwitch", "Twitch"),
                Button.link("https://www.laby.net/$whitelistName", "Minecraft"),
            )
        ).queue()
    }
}