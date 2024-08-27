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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The type Twitch follow command.
 */
@DiscordCommandMeta(
    name = "twitch-follow",
    description = "Fordere einen Benutzer auf CastCrafter auf Twitch zu folgen.",
    permission = CommandPermission.TWITCH_FOLLOW
)
public class TwitchFollowCommand extends TicketCommand {

  @Autowired
  public TwitchFollowCommand(TicketService ticketService) {
    super(ticketService);
  }

  private static final String OPTION_USER = "user";
  private static final String CASTCRAFTER_TWITCH_URL = "https://www.twitch.tv/castcrafter";

  /**
   * Returns the options of the command.
   *
   * @return The options of the command.
   */
  @NotNull
  @Override
  public List<OptionData> getOptions() {
    return List.of(
        new OptionData(
            OptionType.USER,
            OPTION_USER,
            RawMessages.get("interaction.command.ticket.twitch.follow.arg.user"),
            true,
            false
        )
    );
  }

  @Override
  public void internalExecute(@NotNull SlashCommandInteractionEvent interaction,
      InteractionHook hook)
      throws CommandException {
    final User user = getUserOrThrow(interaction, OPTION_USER);
    getChannel().sendMessage(user.getAsMention())
        .setEmbeds(getEmbed())
        .queue();

    hook.deleteOriginal().queue();
  }

  /**
   * Returns the embed for the command.
   *
   * @return The embed.
   */
  private @NotNull MessageEmbed getEmbed() {
    return new EmbedBuilder()
        .setTitle(RawMessages.get("interaction.command.ticket.twitch.follow.embed.title"))
        .setDescription(
            RawMessages.get("interaction.command.ticket.twitch.follow.embed.description",
                CASTCRAFTER_TWITCH_URL))
        .setColor(EmbedColors.TWITCH_EMBED_COLOR)
        .setTimestamp(TimeUtils.berlinTimeProvider().getCurrentTime())
        .build();
  }
}
