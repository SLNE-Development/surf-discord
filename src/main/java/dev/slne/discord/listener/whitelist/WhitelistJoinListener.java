package dev.slne.discord.listener.whitelist;

import dev.slne.discord.annotation.DiscordListener;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.spring.feign.dto.WhitelistDTO;
import dev.slne.discord.spring.service.whitelist.WhitelistService;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * The type WhitelistDTO join listener.
 */
@DiscordListener
public class WhitelistJoinListener extends ListenerAdapter {

  private final WhitelistService whitelistService;

  @Autowired
  public WhitelistJoinListener(WhitelistService whitelistService) {
    this.whitelistService = whitelistService;
  }

  @Override
  public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
    handleEvent(event);
  }

  @Async
  protected void handleEvent(GuildMemberJoinEvent event) {
    final User user = event.getUser();
    final Guild guild = event.getGuild();
    final WhitelistDTO whitelist = whitelistService.getWhitelistByDiscordId(user.getId()).join();

    if (whitelist == null) {
      return;
    }

    final GuildConfig guildConfig = GuildConfig.getConfig(guild.getId());
    if (guildConfig == null) {
      return;
    }

    final Role whitelistedRole = guildConfig.getWhitelistedRole();
    if (whitelistedRole == null) {
      return;
    }

    final Member member = guild.retrieveMember(user).complete();
    if (member == null) {
      return;
    }

    guild.addRoleToMember(member, whitelistedRole).queue();
  }
}
