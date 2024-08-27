package dev.slne.discord.discord.interaction.command.commands.reactionrole;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.config.discord.ReactionRoleConfig;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.EmbedColors;
import dev.slne.discord.message.RawMessages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
    final GuildConfig guildConfig = getGuildConfigOrThrow(interaction);
    final TextChannel textChannel = getTextChannelOrThrow(interaction);

    textChannel.sendMessageEmbeds(getEmbed()).queue(message -> {
      final ReactionRoleConfig reactionRoleConfig = guildConfig.getReactionRole();
      final String reaction;

      if (reactionRoleConfig != null) {
        reaction = reactionRoleConfig.getReaction();
      } else {
        reaction = "🔔";
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
        .setTitle(RawMessages.get("interaction.command.reaction-role.title"))
        .setDescription(RawMessages.get("interaction.command.reaction-role.description"))
        .setColor(EmbedColors.REACTION_ROLE)
        .build();
  }
}
