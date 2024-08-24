package dev.slne.discord.discord.interaction.command.commands.ticket;

import static com.google.common.base.Preconditions.checkNotNull;

import dev.slne.discord.discord.interaction.button.buttons.OpenTicketButton;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import dev.slne.discord.spring.annotation.processor.DiscordButtonProcessor;
import dev.slne.discord.spring.annotation.processor.DiscordButtonProcessor.DiscordButtonHandlerHolder;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * The type Ticket button command.
 */
@DiscordCommandMeta(name = "ticket-buttons", description = "Print the ticket button and embed.", permission = CommandPermission.TICKET_BUTTONS)
public class TicketButtonCommand extends DiscordCommand {

  private final DiscordButtonProcessor discordButtonProcessor;

  public TicketButtonCommand(DiscordButtonProcessor discordButtonProcessor) {
    this.discordButtonProcessor = discordButtonProcessor;
  }

  @Override
  public void execute(SlashCommandInteractionEvent interaction) {
    interaction.deferReply(true).queue(hook -> {
      hook.deleteOriginal().queue();
      final MessageChannelUnion channel = interaction.getChannel();
      final DiscordButtonHandlerHolder openTicketButton = discordButtonProcessor.getHandler(
          OpenTicketButton.ID);

      checkNotNull(openTicketButton, "openTicketButton is null");
      sendEmbed(openTicketButton.toJdaButton(), channel);
    });
  }

  /**
   * Send the embeds
   *
   * @param button  the button
   * @param channel the channel
   */
  private void sendEmbed(Button button, MessageChannel channel) {
    final MessageEmbed embed = new EmbedBuilder()
        .setTitle("Ticket erstellen")
        .setDescription("""
            Du möchtest eine Whitelist-Anfrage stellen, einen Spieler bzw. ein Problem melden oder einen Entbannungsantrag für den Server erstellen, so kannst du hier ein Ticket erstellen.
            
            Bitte mache dich **vorher** mit unterschiedlichen Tickettypen vertraut!
            Die Übersicht findest du hier: https://server.castcrafter.de/support
            
            Allgemeine Fragen sollten in den dafür vorgesehenen öffentlichen Kanälen gestellt werden.
            
            Wir bemühen uns die Tickets schnellstmöglich zu bearbeiten, jedoch arbeitet das gesamte Team freiwillig, und gerade unter der Woche kann die Bearbeitung der Tickets länger dauern.
            """)
        .setColor(Color.GREEN)
        .build();

    channel.sendMessageEmbeds(embed).setActionRow(button).queue();
  }

}
