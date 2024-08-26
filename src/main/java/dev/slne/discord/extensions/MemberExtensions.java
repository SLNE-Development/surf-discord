package dev.slne.discord.extensions;

import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.config.role.RoleConfig;
import java.util.List;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Unmodifiable;

@UtilityClass
@ExtensionMethod({MemberExtensions.class})
public class MemberExtensions {

  @Unmodifiable
  public List<String> getRoleIds(Member this$) {
    return this$.getRoles().stream()
        .map(ISnowflake::getId)
        .toList();
  }

  public boolean isTeamMember(Member this$, GuildConfig guildConfig) {
    final List<String> teamRoleIds = guildConfig.getRoleConfig().values().stream()
        .map(RoleConfig::getDiscordRoleIds)
        .flatMap(List::stream)
        .toList();

    return this$.getRoleIds()
        .stream()
        .anyMatch(teamRoleIds::contains);
  }
}
