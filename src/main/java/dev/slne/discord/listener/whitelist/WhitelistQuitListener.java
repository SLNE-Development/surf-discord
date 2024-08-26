package dev.slne.discord.listener.whitelist;

import dev.slne.discord.annotation.DiscordListener;
import dev.slne.discord.spring.feign.dto.WhitelistDTO;
import dev.slne.discord.spring.service.whitelist.WhitelistService;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

/**
 * The type WhitelistDTO quit listener.
 */
@DiscordListener
public class WhitelistQuitListener extends ListenerAdapter {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("WhitelistQuitListener");
  private final WhitelistService whitelistService;

  public WhitelistQuitListener(WhitelistService whitelistService) {
    this.whitelistService = whitelistService;
  }

  @Override
  public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
    final User user = event.getUser();
    final WhitelistDTO whitelist = whitelistService.getWhitelistByDiscordId(user.getId()).join();

    if (whitelist == null) {
      return;
    }

    whitelist.setBlocked(true);
    final WhitelistDTO updatedWhitelist = whitelistService.updateWhitelist(whitelist).join();

    if (updatedWhitelist == null) {
      LOGGER.error("Failed to update whitelist for user {}.", user.getName());
    } else {
      LOGGER.info("User {} left the server and was blocked.", user.getName());
    }
  }
}
