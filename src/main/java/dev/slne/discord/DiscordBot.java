package dev.slne.discord;

import java.io.FileNotFoundException;
import java.io.IOException;

import dev.slne.discord.discord.interaction.command.DiscordCommandManager;
import dev.slne.discord.discord.interaction.modal.DiscordModalManager;
import dev.slne.discord.discord.settings.BotConnectionFile;
import dev.slne.discord.discord.settings.BotConnectionSettings;
import dev.slne.discord.discord.settings.GatewayIntents;
import dev.slne.discord.listener.ListenerManager;
import dev.slne.discord.ticket.TicketManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordBot {

    private static String botToken;

    private static DiscordBot instance;
    private BotConnectionFile botConnectionFile;
    private JDA jda;

    private ListenerManager listenerManager;
    private DiscordModalManager modalManager;
    private DiscordCommandManager commandManager;
    private TicketManager ticketManager;

    /**
     * Called when the bot is loaded.
     */
    @SuppressWarnings({ "java:S2696" })
    public void onLoad() {
        instance = this;

        try {
            botConnectionFile = new BotConnectionFile();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }

        try {
            BotConnectionSettings settings = botConnectionFile.readObject(BotConnectionSettings.class);
            botToken = settings.getBotToken();
        } catch (IOException e) {
            e.printStackTrace();
        }

        commandManager = new DiscordCommandManager();
        listenerManager = new ListenerManager();
        modalManager = new DiscordModalManager();
        ticketManager = new TicketManager();

        listenerManager.registerDiscordListeners();
        listenerManager.registerListeners();
    }

    /**
     * Called when the bot is enabled.
     */
    public void onEnable() {
        if (botToken == null) {
            Launcher.getLogger().logError("Bot token is null. Please check your bot-connection.json file.");
            System.exit(1000);
            return;
        }

        JDABuilder builder = JDABuilder.createDefault(botToken);

        builder.setAutoReconnect(true);
        builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);
        builder.setEnabledIntents(GatewayIntents.getGatewayIntents());
        builder.setActivity(Activity.watching("Keviro struggle"));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        this.jda = builder.build();
        listenerManager.registerListenersToJda(this.jda);
    }

    /**
     * Called when the bot is disabled.
     */
    public void onDisable() {
        // Currently empty
    }

    /**
     * Gets the instance of the discord bot.
     *
     * @return the instance
     */
    public static DiscordBot getInstance() {
        return instance;
    }

    /**
     * Gets the command manager.
     *
     * @return the command manager
     */
    public DiscordCommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Gets the listener manager.
     *
     * @return the listener manager
     */
    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    /**
     * Gets the modal manager.
     *
     * @return the modal manager
     */
    public DiscordModalManager getModalManager() {
        return modalManager;
    }

    /**
     * Gets the ticket manager.
     *
     * @return the ticket manager
     */
    public TicketManager getTicketManager() {
        return ticketManager;
    }

    /**
     * Gets the jda.
     *
     * @return the jda
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Gets the bot connection file.
     *
     * @return the bot connection file
     */
    public BotConnectionFile getBotConnectionFile() {
        return botConnectionFile;
    }
}
