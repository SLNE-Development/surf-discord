package dev.slne.discord.discord.interaction.command.commands.ticket.utils;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.EmbedColors;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.util.TimeUtils;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The type Twitch connect command.
 */
@DiscordCommandMeta(
    name = "twitch-connect",
    description = "Fordere einen Benutzer auf seinen Twitch-Account mit dem Discord-Account zu verbinden.",
    permission = CommandPermission.TWITCH_CONNECT
)
public class TwitchConnectCommand extends TicketCommand {

  private static final String OPTION_USER = "user";
  private static final String TWITCH_CONNECT_TUTORIAL_URL = "https://server.castcrafter.de/support.html#link-twitch";

  @Autowired
  public TwitchConnectCommand(TicketService ticketService) {
    super(ticketService);
  }

  @Override
  public @Nonnull List<OptionData> getOptions() {
    return List.of(
        new OptionData(
            OptionType.USER,
            OPTION_USER,
            RawMessages.get("interaction.command.ticket.twitch.connect.arg.user"),
            true,
            false
        )
    );
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final User user = getUserOrThrow(interaction, OPTION_USER);

    getChannel().sendMessage(user.getAsMention())
        .setEmbeds(getEmbed())
        .queue();

    hook.deleteOriginal().queue();
  }

  /**
   * Gets the embed for the command.
   *
   * @return The embed.
   */
  public MessageEmbed getEmbed() {
    return new EmbedBuilder()
        .setTitle(RawMessages.get("interaction.command.ticket.twitch.connect.embed.title"))
        .setDescription(
            RawMessages.get("interaction.command.ticket.twitch.connect.embed.description",
                TWITCH_CONNECT_TUTORIAL_URL))
        .setColor(EmbedColors.TWITCH_EMBED_COLOR)
        .setTimestamp(TimeUtils.berlinTimeProvider().getCurrentTime())
        .build();
  }
}
