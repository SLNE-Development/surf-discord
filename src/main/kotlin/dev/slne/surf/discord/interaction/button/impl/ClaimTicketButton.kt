package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.util.asTicketOrThrow
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.stereotype.Component

@Component
class ClaimTicketButton(
    private val ticketService: TicketService
) : DiscordButton {
    override val id = "ticket:claim"
    override val button = Button.of(
        ButtonStyle.SECONDARY,
        id,
        "Claim",
        Emoji.fromCustom("information", 1433420020993757325, false)
    )

    override suspend fun onClick(event: ButtonInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_CLAIM)) {
            event.reply("Dazu hast du keine Berechtigung.").setEphemeral(true).queue()
            return
        }

        val ticket = event.hook.asTicketOrThrow()

        if (ticketService.isClaimedByUser(ticket, event.user)) {
            ticketService.unclaim(ticket)
            event.reply("Du hast das Ticket freigegeben.").setEphemeral(true).queue()
        } else {
            if (ticketService.isClaimed(ticket)) {
                event.reply("Das Ticket wurde bereits von einer anderen Person geclaimt.")
                    .setEphemeral(true).queue()
            } else {
                ticketService.claim(ticket, event.user)
                event.reply("Du hast das Ticket geclaimt.").setEphemeral(true).queue()
            }
        }
    }
}