package dev.slne.discord.discord.interaction.command.commands.ticket;

import static com.google.common.base.Preconditions.checkNotNull;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.discord.interaction.button.buttons.OpenTicketButton;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.EmbedColors;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.processor.DiscordButtonProcessor;
import dev.slne.discord.spring.processor.DiscordButtonProcessor.DiscordButtonHandlerHolder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    hook.deleteOriginal().queue();
    final MessageChannelUnion channel = interaction.getChannel();
    final DiscordButtonHandlerHolder openTicketButton = discordButtonProcessor.getHandler(
        OpenTicketButton.ID);

    checkNotNull(openTicketButton, "openTicketButton is null");
    sendEmbed(openTicketButton.toJdaButton(), channel);
  }

  /**
   * Send the embeds
   *
   * @param button  the button
   * @param channel the channel
   */
  private void sendEmbed(Button button, MessageChannel channel) {
    final MessageEmbed embed = new EmbedBuilder()
        .setTitle(RawMessages.get("interaction.command.ticket.ticket-button.title"))
        .setDescription(RawMessages.get("interaction.command.ticket.ticket-button.description"))
        .setColor(EmbedColors.CREATE_TICKET)
        .build();

    channel.sendMessageEmbeds(embed).setActionRow(button).queue();
  }

}
