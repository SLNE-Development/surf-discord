package dev.slne.discord.discord.settings;

import java.util.List;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.Unmodifiable;

/**
 * The type Gateway intents.
 */
@UtilityClass
public class GatewayIntents {

  /**
   * Returns the gateway intents.
   *
   * @return The gateway intents.
   */
  @Unmodifiable
  public List<GatewayIntent> getGatewayIntents() {
    return List.of(
        // Emoji
        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,

        // Guild
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.SCHEDULED_EVENTS,

        // Guild Messages
        GatewayIntent.MESSAGE_CONTENT,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MESSAGE_TYPING,

        // Direct Messages
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.DIRECT_MESSAGE_TYPING
    );
  }

}
