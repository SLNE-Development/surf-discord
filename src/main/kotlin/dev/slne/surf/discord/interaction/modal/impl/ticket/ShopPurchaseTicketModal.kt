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
class ShopPurchaseTicketModal(
    private val ticketService: TicketService,
    private val buttonRegistry: ButtonRegistry,
) : DiscordModal {
    override val id = "ticket:shop:purchase"

    override fun create() = modal(id, translatable("ticket.shop.purchase.modal.title")) {
        textInput {
            id = "minecraft_name"
            label = translatable("ticket.shop.purchase.modal.field.minecraft_name.label")
            style = TextInputStyle.SHORT
            lengthRange = 3..16
            placeholder = translatable("ticket.shop.purchase.modal.field.minecraft_name.placeholder")
            required = true
        }

        textInput {
            id = "order_id"
            label = translatable("ticket.shop.purchase.modal.field.order_id.label")
            style = TextInputStyle.SHORT
            lengthRange = 3..100
            placeholder = translatable("ticket.shop.purchase.modal.field.order_id.placeholder")
            required = false
        }

        textInput {
            id = "issue"
            label = translatable("ticket.shop.purchase.modal.field.issue.label")
            style = TextInputStyle.PARAGRAPH
            lengthRange = 10..4000
            placeholder = translatable("ticket.shop.purchase.modal.field.issue.placeholder")
            required = true
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction
        val user = interaction.user

        val minecraftName = interaction.getValue("minecraft_name")?.asString ?: return
        val orderId = interaction.getValue("order_id")?.asString?.takeIf { it.isNotBlank() }
        val issue = interaction.getValue("issue")?.asString ?: return

        interaction.reply(translatable("ticket.creating")).setEphemeral(true).queue()

        val ticket =
            ticketService.createTicket(
                interaction.hook,
                TicketType.SHOP_PURCHASE,
                buildMap {
                    put("minecraft_name", minecraftName)
                    orderId?.let { put("order_id", it) }
                    put("issue", issue)
                }
            ) ?: run {
                if (ticketService.hasOpenTicket(user.idLong, TicketType.SHOP_PURCHASE)) {
                    interaction.hook.editOriginal(translatable("ticket.shop.purchase.already_open"))
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
                title = translatable("ticket.shop.purchase.embed.title")
                description = translatable("ticket.shop.purchase.embed.description")
                color = Colors.SUCCESS

                field {
                    name = translatable("ticket.shop.purchase.embed.field.minecraft_name")
                    value = minecraftName
                    inline = true
                }

                orderId?.let {
                    field {
                        name = translatable("ticket.shop.purchase.embed.field.order_id")
                        value = it
                        inline = true
                    }
                }

                issue.chunked(1024).forEach { chunk ->
                    field {
                        name = translatable("ticket.shop.purchase.embed.field.issue")
                        value = chunk
                        inline = false
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
