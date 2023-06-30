package dev.slne.discord;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.pusher.PusherModule;
import dev.slne.data.core.pusher.packet.PusherPacket;
import dev.slne.discord.datasource.pusher.packets.TicketClosePacket;
import dev.slne.discord.datasource.pusher.packets.TicketOpenPacket;
import dev.slne.discord.datasource.pusher.packets.TicketReOpenPacket;
import dev.slne.discord.discord.guild.role.DiscordRoleManager;
import dev.slne.discord.discord.interaction.button.DiscordButtonManager;
import dev.slne.discord.discord.interaction.command.DiscordCommandManager;
import dev.slne.discord.discord.interaction.modal.DiscordModalManager;
import dev.slne.discord.discord.settings.BotConnectionFile;
import dev.slne.discord.discord.settings.BotConnectionSettings;
import dev.slne.discord.discord.settings.GatewayIntents;
import dev.slne.discord.listener.ListenerManager;
import dev.slne.discord.listener.event.events.BotStartEvent;
import dev.slne.discord.ticket.TicketManager;
import dev.slne.discord.whitelist.UUIDCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordBot {

    private static String botToken;

    private static DiscordBot instance;
    private BotConnectionFile botConnectionFile;
    private JDA jda;

    private DiscordRoleManager roleManager;
    private ListenerManager listenerManager;
    private DiscordModalManager modalManager;
    private DiscordCommandManager commandManager;
    private TicketManager ticketManager;
    private DiscordButtonManager buttonManager;
    private UUIDCache uuidCache;

    /**
     * Called when the bot is loaded.
     */
    @SuppressWarnings({ "java:S2696", "java:S2142" })
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

        JDABuilder builder = JDABuilder.createDefault(botToken);

        builder.setAutoReconnect(true);
        builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);
        builder.setEnabledIntents(GatewayIntents.getGatewayIntents());
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        this.jda = builder.build();
        try {
            this.jda.awaitReady();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        roleManager = new DiscordRoleManager();

        uuidCache = new UUIDCache();
        commandManager = new DiscordCommandManager();
        listenerManager = new ListenerManager();
        modalManager = new DiscordModalManager();
        ticketManager = new TicketManager();

        listenerManager.registerDiscordListeners();
        listenerManager.registerListeners();

        for (Guild guild : jda.getGuilds()) {
            DiscordBot.getInstance().getCommandManager().clearGuild(guild)
                    .whenComplete(cb -> DiscordBot.getInstance().getCommandManager().registerToGuild(guild));
        }

        DiscordBot.getInstance().getTicketManager().fetchActiveTickets();

        listenerManager.broadcastEvent(new BotStartEvent());
        Launcher.getLogger().logInfo("Bot is ready!");
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

        PusherModule pusherModule = DataApi.getDataInstance().getDataModule("pusher");
        Map<String, Class<? extends PusherPacket>> packets = new HashMap<>();

        packets.put("App\\Events\\Ticket\\CloseTicketEvent", TicketClosePacket.class);
        packets.put("App\\Events\\Ticket\\OpenTicketEvent", TicketOpenPacket.class);
        packets.put("App\\Events\\Ticket\\ReOpenTicketEvent", TicketReOpenPacket.class);

        for (Map.Entry<String, Class<? extends PusherPacket>> entry : packets.entrySet()) {
            pusherModule.getPacketManager().registerPacket(entry.getKey(), entry.getValue());
        }

        pusherModule.bindEvents("surf-communication",
                packets.keySet().toArray(new String[packets.keySet().size()]));

        listenerManager.registerListenersToJda(this.jda);

        buttonManager = new DiscordButtonManager();
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

    /**
     * @return the buttonManager
     */
    public DiscordButtonManager getButtonManager() {
        return buttonManager;
    }

    /**
     * @return the botToken
     */
    public String getBotToken() {
        return botToken;
    }

    /**
     * @return the uuidCache
     */
    public UUIDCache getUuidCache() {
        return uuidCache;
    }

    /**
     * @return the roleManager
     */
    public DiscordRoleManager getRoleManager() {
        return roleManager;
    }
}
