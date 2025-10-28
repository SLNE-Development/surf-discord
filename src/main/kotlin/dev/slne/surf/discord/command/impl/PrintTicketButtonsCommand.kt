package dev.slne.surf.discord.command.impl

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.command.dsl.embed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import java.awt.Color

@Component
@DiscordCommand("ticketbuttons", "Sendet die Ticket Buttons in den aktuellen Kanal.")
class PrintTicketButtonsCommand : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        event.messageChannel.sendMessageEmbeds(
            embed {
                title = "Ticket erstellen"
                description =
                    "Du möchtest eine Whitelist-Anfrage stellen, einen Spieler bzw. ein Problem melden oder einen Entbannungsantrag für den Server erstellen, so kannst du hier ein Ticket erstellen.\n" +
                            "\n" +
                            "Bitte mache dich vorher mit unterschiedlichen Tickettypen vertraut!\n" +
                            "Die Übersicht findest du hier: https://server.castcrafter.de/support\n" +
                            "\n" +
                            "Allgemeine Fragen sollten in den dafür vorgesehenen öffentlichen Kanälen gestellt werden.\n" +
                            "\n" +
                            "Wir bemühen uns die Tickets schnellstmöglich zu bearbeiten, jedoch arbeitet das gesamte Team freiwillig, und gerade unter der Woche kann die Bearbeitung der Tickets länger dauern."

                color = Color(197, 239, 72)
            }
        ).addActionRow(
            Button.success("surf-discord-open-ticket", "🎫 Ticket öffnen")
        ).queue {
            event.reply("Die Ticket Buttons wurden erfolgreich gesendet.").setEphemeral(true)
                .queue()
        }
    }
}