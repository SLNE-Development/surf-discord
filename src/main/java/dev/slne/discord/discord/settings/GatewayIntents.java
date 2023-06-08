package dev.slne.discord.discord.settings;

import java.util.Collection;
import java.util.List;

import net.dv8tion.jda.api.requests.GatewayIntent;

public class GatewayIntents {

    /**
     * Private constructor to hide the implicit public one.
     */
    private GatewayIntents() {
    }

    /**
     * Returns the gateway intents.
     *
     * @return The gateway intents.
     */
    public static final Collection<GatewayIntent> getGatewayIntents() {
        return List.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES,

                // Guild Messages
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGE_TYPING,

                // Direct Messages
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGE_TYPING);
    }

}
