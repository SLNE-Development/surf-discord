package dev.slne.discord.discord.interaction.command;

import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.command.CommandExceptions;
import dev.slne.discord.util.ExceptionFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

public interface CommandUtil {

  default <T extends Throwable> void checkUserNotBot(
      @NotNull User user,
      @NotNull JDA jda,
      ExceptionFactory<T> exceptionFactory
  ) throws T {
    if (user.equals(jda.getSelfUser())) {
      throw exceptionFactory.create();
    }
  }

  default @NotNull Guild getGuildOrThrow(Interaction interaction) throws CommandException {
    final Guild guild = interaction.getGuild();

    if (guild == null) {
      throw CommandExceptions.NO_GUILD.create();
    }

    return guild;
  }

  default GuildConfig getGuildConfigOrThrow(Interaction interaction) throws CommandException {
    return getGuildConfigOrThrow(getGuildOrThrow(interaction));
  }

  default GuildConfig getGuildConfigOrThrow(@NotNull Guild guild) throws CommandException {
    final GuildConfig guildConfig = GuildConfig.getByGuildId(guild.getId());

    if (guildConfig == null) {
      throw CommandExceptions.SERVER_NOT_REGISTERED.create();
    }

    return guildConfig;

  }

  default @NotNull TextChannel getTextChannelOrThrow(Interaction interaction)
      throws CommandException {
    if (!(interaction.getChannel() instanceof TextChannel textChannel)) {
      throw CommandExceptions.NO_TEXT_CHANNEL.create();
    }

    return textChannel;
  }
}
