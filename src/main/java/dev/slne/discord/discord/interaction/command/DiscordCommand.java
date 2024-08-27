package dev.slne.discord.discord.interaction.command;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.config.role.RoleConfig;
import dev.slne.discord.exception.DiscordException;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.command.CommandExceptions;
import dev.slne.discord.exception.command.pre.PreCommandCheckException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.RawMessages;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Discord command.
 */
@Getter
public abstract class DiscordCommand implements CommandUtil {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("DiscordCommand");

  @Nonnull
  private final DefaultMemberPermissions defaultMemberPermissions;

  /**
   * Creates a new DiscordCommand.
   */
  protected DiscordCommand() {
    this.defaultMemberPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);
  }

  /**
   * Returns the subcommands of the command.
   *
   * @return The subcommands of the command.
   */
  public @Nonnull List<SubcommandData> getSubCommands() {
    return List.of();
  }

  /**
   * Returns the options of the command.
   *
   * @return The options of the command.
   */
  public @Nonnull List<OptionData> getOptions() {
    return List.of();
  }

  /**
   * Executes the command internally
   *
   * @param interaction the interaction event
   */
  public final void execute(SlashCommandInteractionEvent interaction) {
    executeAsync(interaction);
  }

  @Async
  protected void executeAsync(SlashCommandInteractionEvent interaction) {
    final User user = interaction.getUser();
    final Guild guild = interaction.getGuild();

    interaction.deferReply(true).queue(hook -> {
      try {
        if (performDiscordCommandChecks(user, guild, interaction, hook).join()
            && performAdditionalChecks(user, guild, interaction, hook).join()) {
          internalExecute(interaction, hook);
        }
      } catch (DiscordException e) {
        hook.editOriginal(e.getMessage()).queue();
        LOGGER.error("Error while executing command", e);
      }
    });
  }

  protected CompletableFuture<Boolean> performAdditionalChecks(
      User user,
      Guild guild,
      SlashCommandInteractionEvent interaction,
      InteractionHook hook
  ) throws PreCommandCheckException {
    return CompletableFuture.completedFuture(true);
  }

  /**
   * Performs the checks for the command.
   *
   * @param user        The user.
   * @param guild       The guild.
   * @param interaction The interaction.
   * @return Whether the checks were successful.
   */
  @Async
  @NonExtendable
  protected CompletableFuture<Boolean> performDiscordCommandChecks(
      User user,
      Guild guild,
      SlashCommandInteractionEvent interaction,
      InteractionHook hook
  ) throws PreCommandCheckException {
    try {
      getGuildOrThrow(interaction);
    } catch (CommandException e) {
      throw new PreCommandCheckException(e);
    }

    final Member member = guild.retrieveMember(user).complete();
    if (member == null) {
      throw new PreCommandCheckException(CommandExceptions.GENERIC.create());
    }

    final List<Role> memberDiscordRoles = member.getRoles();
    final List<RoleConfig> memberRoles = memberDiscordRoles.stream()
        .map(role -> RoleConfig.getDiscordRoleRoles(guild.getId(), role.getId()))
        .flatMap(List::stream)
        .toList();

    boolean hasPermission = memberRoles.stream()
        .anyMatch(role -> role.hasCommandPermission(getPermission()));

    if (!hasPermission) {
      hook.editOriginal(RawMessages.get("error.command.no-permission")).queue();
      return CompletableFuture.completedFuture(false);
    } else {
      return CompletableFuture.completedFuture(true);
    }
  }

  protected final CommandPermission getPermission() {
    return this.getClass().getAnnotation(DiscordCommandMeta.class).permission();
  }

  /**
   * Executes the command.
   *
   * @param interaction The interaction.
   */
  public abstract void internalExecute(SlashCommandInteractionEvent interaction,
      InteractionHook hook) throws CommandException;

  protected final <R> Optional<R> getOption(
      @NotNull CommandInteractionPayload interaction,
      String name,
      Function<OptionMapping, R> mapper
  ) {
    final OptionMapping option = interaction.getOption(name);

    if (option == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(mapper.apply(option));
  }

  protected final <R> R getOptionOrThrow(
      @NotNull CommandInteractionPayload interaction,
      String name,
      Function<OptionMapping, R> mapper,
      @Language("markdown") String errorMessage
  ) throws CommandException {
    return getOption(interaction, name, mapper)
        .orElseThrow(() -> new CommandException(errorMessage));
  }

  protected final Optional<User> getUser(
      @NotNull CommandInteractionPayload interaction,
      String name
  ) {
    return getOption(interaction, name, OptionMapping::getAsUser);
  }

  protected final User getUserOrThrow(
      @NotNull CommandInteractionPayload interaction,
      String name
  ) throws CommandException {
    return getUser(interaction, name)
        .orElseThrow(CommandExceptions.ARG_MISSING_USER::create);
  }

  protected final Optional<String> getString(
      @NotNull CommandInteractionPayload interaction,
      String name
  ) {
    return getOption(interaction, name, OptionMapping::getAsString);
  }

  protected final String getStringOrThrow(
      @NotNull CommandInteractionPayload interaction,
      String name,
      @Language("markdown") String errorMessage
  ) throws CommandException {
    return getString(interaction, name)
        .orElseThrow(() -> new CommandException(errorMessage));
  }
}
