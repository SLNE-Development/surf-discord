package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.util.Emojis
import dev.slne.surf.discord.util.asTicketOrThrow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

object ClaimTicketButton : DiscordButton {
    override val id = "ticket:claim"
    override val button by lazy {
        Button.of(
            ButtonStyle.SECONDARY,
            id,
            translatable("button.ticket.claim"),
            Emojis.information
        )
    }

    override suspend fun onClick(event: ButtonInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_CLAIM)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val ticket = event.hook.asTicketOrThrow()

        if (TicketService.isClaimedByUser(ticket, event.user)) {
            TicketService.unclaim(ticket, event.user)
            event.reply(translatable("claim.unclaimed")).setEphemeral(true).queue()
        } else {
            if (TicketService.isClaimed(ticket)) {
                event.reply(translatable("claim.already-claimed"))
                    .setEphemeral(true).queue()
            } else {
                TicketService.claim(ticket, event.user)
                event.reply(translatable("claim.claimed")).setEphemeral(true).queue()
            }
        }
    }
}