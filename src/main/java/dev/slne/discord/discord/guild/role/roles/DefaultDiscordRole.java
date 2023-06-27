package dev.slne.discord.discord.guild.role.roles;

import java.util.List;

import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.guild.role.DiscordRole;

public class DefaultDiscordRole extends DiscordRole {

    /**
     * Create a new default role with the name "Default" and the following
     * permissions:
     * <ul>
     * <li>VIEW_CHANNEL</li>
     * <li>SEND_MESSAGES</li>
     * <li>SEND_MESSAGES_IN_THREADS</li>
     * <li>EMBED_LINKS</li>
     * <li>ATTACH_FILES</li>
     * <li>ADD_REACTIONS</li>
     * <li>USE_EXTERNAL_EMOJIS</li>
     * <li>USE_EXTERNAL_STICKERS</li>
     * <li>READ_MESSAGE_HISTORY</li>
     * <li>USE_APPLICATION_COMMANDS</li>
     * </ul>
     */
    public DefaultDiscordRole() {
        super(DiscordRole.DEFAULT_ROLE, List.of(
                DiscordPermission.VIEW_CHANNEL,
                DiscordPermission.SEND_MESSAGES,
                DiscordPermission.SEND_MESSAGES_IN_THREADS,
                DiscordPermission.EMBED_LINKS,
                DiscordPermission.ATTACH_FILES,
                DiscordPermission.ADD_REACTIONS,
                DiscordPermission.USE_EXTERNAL_EMOJIS,
                DiscordPermission.USE_EXTERNAL_STICKERS,
                DiscordPermission.READ_MESSAGE_HISTORY,
                DiscordPermission.USE_APPLICATION_COMMANDS));
    }

}
