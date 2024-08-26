package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
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
public class WhitelistRoleRemoveCommand extends DiscordCommand {

  private static final ComponentLogger LOGGER = ComponentLogger.logger(
      "WhitelistRoleRemoveCommand");

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final Guild guild = interaction.getGuild();

    if (guild == null) {
      throw new CommandException("Du musst auf einem Server sein, um diesen Command auszuführen.");
    }

    final GuildConfig guildConfig = GuildConfig.getConfig(guild.getId());

    if (guildConfig == null) {
      throw new CommandException("Dieser Server ist nicht registriert.");
    }

    final Role whitelistedRole = guild.getRoleById(guildConfig.getWhitelistRoleId());

    if (whitelistedRole == null) {
      throw new CommandException("Die Whitelist Rolle ist nicht registriert.");
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

    guild.findMembersWithRoles(whitelistedRole).onSuccess(members -> {
      for (final Member member : members) {
        if (member == null) {
          continue;
        }

        futures.add(guild.removeRoleFromMember(member, whitelistedRole).submit());
      }

      CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
          .thenRun(() -> hook.editOriginal("%s Benutzer wurden aus der WhitelistDTO Rolle entfernt."
                  .formatted(members.size()))
              .queue());
    }).onError(error -> {
      LOGGER.error("Error while removing role from members", error);
      hook.editOriginal("Es ist ein Fehler aufgetreten.")
          .queue();
    });
  }
}
