package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.command.CommandExceptions;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.RawMessages;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;

/**
 * The type WhitelistDTO role remove command.
 */
@DiscordCommandMeta(
    name = "wlrole",
    description = "Entfernt alle Benutzer aus der Whitelist Rolle.",
    permission = CommandPermission.WHITELIST_ROLE
)
public class WhitelistRoleRemoveCommand extends
    DiscordCommand { // TODO: 27.08.2024 19:52 - make console command?

  private static final ComponentLogger LOGGER = ComponentLogger.logger(
      "WhitelistRoleRemoveCommand");

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final Guild guild = getGuildOrThrow(interaction);
    final GuildConfig guildConfig = getGuildConfigOrThrow(guild);
    final Role whitelistedRole = guild.getRoleById(guildConfig.getWhitelistRoleId());

    if (whitelistedRole == null) {
      throw CommandExceptions.WHITELIST_ROLE_NOT_REGISTERED.create();
    }

    removeRoleFromMembers(guild, whitelistedRole, hook);
  }

  @Async
  protected void removeRoleFromMembers(
      @NotNull Guild guild,
      @NotNull Role whitelistedRole,
      @NotNull InteractionHook hook
  ) {
    final List<CompletableFuture<?>> futures = new ArrayList<>();

    hook.editOriginal(RawMessages.get("interaction.command.ticket.whitelist.role.remove.removing"))
        .queue();

    guild.findMembersWithRoles(whitelistedRole).onSuccess(members -> {
      for (final Member member : members) {
        if (member == null) {
          continue;
        }

        futures.add(guild.removeRoleFromMember(member, whitelistedRole).submit());
      }

      CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
          .thenRun(() -> hook.editOriginal(
                  RawMessages.get("interaction.command.ticket.whitelist.role.remove.removed",
                      members.size()))
              .queue());
    }).onError(error -> {
      LOGGER.error("Error while removing role from members", error);
      hook.editOriginal(RawMessages.get("error.generic")).queue();
    });
  }
}
