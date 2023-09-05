package dev.slne.discord;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.gson.GsonConverter;
import dev.slne.data.api.pusher.PusherModule;
import dev.slne.data.api.pusher.packet.PusherPacket;
import dev.slne.discord.datasource.pusher.packets.TicketClosePacket;
import dev.slne.discord.datasource.pusher.packets.TicketOpenPacket;
import dev.slne.discord.datasource.pusher.packets.TicketReOpenPacket;
import dev.slne.discord.datasource.pusher.packets.social.SocialChatPacket;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private GsonConverter gsonConverter;

    /**
     * Gets the instance of the discord bot.
     *
     * @return the instance
     */
    public static DiscordBot getInstance() {
        return instance;
    }

    /**
     * Called when the bot is loaded.
     */
    @SuppressWarnings({ "java:S2696", "java:S2142", "java:S125" })
    public void onLoad() {
        instance = this;

        this.gsonConverter = new GsonConverter();

        try {
            botConnectionFile = new BotConnectionFile();
        } catch (FileNotFoundException exception) {
            DataApi.getDataInstance().logError(getClass(), "Bot connection file not found.", exception);
        }

        try {
            BotConnectionSettings settings = botConnectionFile.readObject(BotConnectionSettings.class);
            botToken = settings.getBotToken();
        } catch (IOException exception) {
            DataApi.getDataInstance().logError(getClass(), "Failed to read bot connection file.", exception);
        }

        JDABuilder builder = JDABuilder.createDefault(botToken);

        builder.setAutoReconnect(true);
        builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.ONLINE_STATUS, CacheFlag.ROLE_TAGS, CacheFlag.STICKER,
                CacheFlag.SCHEDULED_EVENTS);
        builder.disableCache(CacheFlag.VOICE_STATE);
        builder.setEnabledIntents(GatewayIntents.getGatewayIntents());
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        this.jda = builder.build();
        try {
            this.jda.awaitReady();
        } catch (InterruptedException exception) {
            DataApi.getDataInstance().logError(getClass(), "Failed to await ready.", exception);
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
            DiscordBot.getInstance().getCommandManager().registerToGuild(guild);
        }

        DiscordBot.getInstance().getTicketManager().fetchActiveTickets();

        listenerManager.broadcastEvent(new BotStartEvent());
        DataApi.getDataInstance().logInfo(getClass(), "Discord Bot is ready");
    }

    /**
     * Called when the bot is enabled.
     */
    public void onEnable() {
        if (botToken == null) {
            DataApi.getDataInstance()
                    .logError(getClass(), "Bot token is null. Please check your bot-connection.json file.");
            System.exit(1000);
            return;
        }

        PusherModule pusherModule = DataApi.getDataInstance().getDataModule("pusher");
        Map<String, Class<? extends PusherPacket>> packets = new HashMap<>();

        packets.put("surf:ticket:close", TicketClosePacket.class);
        packets.put("surf:ticket:open", TicketOpenPacket.class);
        packets.put("surf:ticket:reopen", TicketReOpenPacket.class);
        packets.put("surf:social:chat", SocialChatPacket.class);

        for (Map.Entry<String, Class<? extends PusherPacket>> entry : packets.entrySet()) {
            pusherModule.getPacketManager().registerPacket(entry.getKey(), entry.getValue());
        }

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

    /**
     * Returns the GsonConverter.
     *
     * @return the GsonConverter
     */
    public GsonConverter getGsonConverter() {
        return gsonConverter;
    }
}
