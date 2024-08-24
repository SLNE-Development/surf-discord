package dev.slne.discord.discord.interaction.command.commands.reactionrole;

import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.config.discord.ReactionRoleConfig;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

/**
 * The type Reaction role text command.
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
@DiscordCommandMeta(
    name = "reactionrole",
    description = "Posted den reaction role text.",
    permission = CommandPermission.REACTION_ROLE_TEXT
)
public class ReactionRoleTextCommand extends DiscordCommand {

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final Guild guild = interaction.getGuild();

    if (guild == null) {
      throw new CommandException("Du musst auf einem Server sein, um diesen Command auszuführen!");
    }

    final GuildConfig guildConfig = GuildConfig.getByGuildId(guild.getId());

    if (guildConfig == null) {
      throw new CommandException("Dieser Server ist nicht registriert!");
    }

    final Channel channel = interaction.getChannel();

    if (!(channel instanceof TextChannel textChannel)) {
      throw new CommandException("Dieser Command kann nur in Textkanälen ausgeführt werden!");
    }

    textChannel.sendMessageEmbeds(getEmbed()).queue(message -> {
      final ReactionRoleConfig reactionRoleConfig = guildConfig.getReactionRole();
      final String reaction;

      if (reactionRoleConfig != null) {
        reaction = reactionRoleConfig.getReaction();
      } else {
        reaction = "\uD83D\uDD14";
      }

      final Emoji emoji = Emoji.fromFormatted(reaction);

      hook.deleteOriginal().queue();
      message.addReaction(emoji).queue();
    });
  }

  /**
   * Returns the embed.
   *
   * @return The embed.
   */
  private @NotNull MessageEmbed getEmbed() {
    return new EmbedBuilder()
        .setTitle("Server Benachrichtigungs-Rolle")
        .setDescription(
            "Du möchtest benachrichtigt werden, wenn es neue Updates gibt? Dann reagiere mit :bell: unter dieser Nachricht!")
        .setColor(Color.decode("#ffaa39"))
        .build();
  }
}
