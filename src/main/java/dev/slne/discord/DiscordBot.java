package dev.slne.discord;

import java.io.FileNotFoundException;
import java.io.IOException;

import dev.slne.discord.datasource.BotConnectionFile;
import dev.slne.discord.datasource.BotConnectionSettings;
import dev.slne.discord.interaction.command.DiscordCommandManager;
import dev.slne.discord.interaction.modal.DiscordModalManager;
import dev.slne.discord.listeners.ListenerManager;
import dev.slne.discord.settings.GatewayIntents;
import dev.slne.discord.ticket.TicketManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordBot {

    private static String BOT_TOKEN;

    private static DiscordBot instance;
    private BotConnectionFile botConnectionFile;
    private JDA jda;

    private ListenerManager listenerManager;
    private DiscordModalManager modalManager;
    private DiscordCommandManager commandManager;
    private TicketManager ticketManager;

    public void onLoad() {
        instance = this;

        try {
            botConnectionFile = new BotConnectionFile();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }

        try {
            BotConnectionSettings settings = botConnectionFile.readObject(BotConnectionSettings.class);
            BOT_TOKEN = settings.getBotToken();
        } catch (IOException e) {
            e.printStackTrace();
        }

        commandManager = new DiscordCommandManager();
        listenerManager = new ListenerManager();
        modalManager = new DiscordModalManager();
        ticketManager = new TicketManager();

        listenerManager.registerListeners();
    }

    public void onEnable() {
        if (BOT_TOKEN == null) {
            System.err.println("Bot token is null. Please check your bot-connection.json file.");
            System.exit(1000);
            return;
        }

        JDABuilder builder = JDABuilder.createDefault(BOT_TOKEN);

        builder.setAutoReconnect(true);
        builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);
        builder.setEnabledIntents(GatewayIntents.getGatewayIntents());
        builder.setActivity(Activity.watching("Keviro struggle"));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        this.jda = builder.build();
        listenerManager.registerListenersToJda(this.jda);
    }

    public void onDisable() {

    }

    public static DiscordBot getInstance() {
        return instance;
    }

    public DiscordCommandManager getCommandManager() {
        return commandManager;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public DiscordModalManager getModalManager() {
        return modalManager;
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public JDA getJda() {
        return jda;
    }

    public BotConnectionFile getBotConnectionFile() {
        return botConnectionFile;
    }
}
