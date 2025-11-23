package dev.slne.surf.discord.ticket.command

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.interaction.selectmenu.SelectMenuRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
@DiscordCommand("close", "Ticket schließen")
class CloseTicketCommand(
    private val selectMenuRegistry: SelectMenuRegistry
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_CLOSE)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val selectMenu = selectMenuRegistry.get("ticket:close:reason").create(event.hook)

        event.reply(translatable("ticket.close.selectreason")).setEphemeral(true).addComponents(
            ActionRow.of(selectMenu)
        ).queue()
    }
}