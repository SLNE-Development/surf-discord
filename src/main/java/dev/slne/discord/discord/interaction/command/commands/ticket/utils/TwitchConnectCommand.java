package dev.slne.discord.discord.interaction.command.commands.ticket.utils;

import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import dev.slne.discord.spring.service.ticket.TicketService;
import java.awt.Color;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.intellij.lang.annotations.Language;
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
  public static final int TWITCH_EMBED_COLOR = 0x6441A5;
  @Language("Markdown")
  private static final String TWITCH_CONNECT_TUTORIAL = "[Zum Tutorial](https://server.castcrafter.de/support.html#link-twitch)";

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
            "Der Nutzer, der seinen Twitch-Account verbinden soll.",
            true,
            false
        )
    );
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final OptionMapping userOption = interaction.getOption(OPTION_USER);

    if (userOption == null) {
      throw new CommandException("Du musst einen Nutzer angeben.");
    }

    final User user = userOption.getAsUser();
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
        .setTitle("Twitch-Account verbinden")
        .setDescription(
            "Bitte verbinde deinen Twitch-Account mit Discord, um auf dem Server zu spielen. Wie du dies tun kannst, findest du hier: "
            + TWITCH_CONNECT_TUTORIAL)
        .setColor(TWITCH_EMBED_COLOR)
        .setTimestamp(Instant.now())
        .build();
  }
}
