package dev.slne.discord.config.discord;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.config.BotConfig;
import dev.slne.discord.config.role.RoleConfig;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Role;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * The interface Guild config.
 */
@Getter
@ConfigSerializable
@ToString
public class GuildConfig {

  private String guildId;
  private String categoryId;
  private String whitelistRoleId;

  private Map<String, RoleConfig> roleConfig;

  /**
   * Instantiates a new Guild config.
   */
  private GuildConfig() {

  }

  /**
   * Gets config.
   *
   * @param guildName the guild id
   * @return the config
   */
  public static GuildConfig getConfig(String guildName) {
    return BotConfig.getConfig().getGuildConfig().get(guildName);
  }

  /**
   * Gets by guild id.
   *
   * @param guildId the guild id
   * @return the by guild id
   */
  public static GuildConfig getByGuildId(String guildId) {
    return BotConfig.getConfig().getGuildConfig().values().stream()
        .filter(guildConfig -> guildConfig.getGuildId().equals(guildId))
        .findFirst()
        .orElse(null);
  }

  /**
   * Instantiates a new Get whitelisted role.
   *
   * @return the whitelisted role
   */
  public Role getWhitelistedRole() {
    return DiscordBot.getInstance().getJda().getRoleById(whitelistRoleId);
  }
}
