package dev.slne.discord.datasource;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.DataSource;
import dev.slne.data.api.pusher.packet.PusherPacket;
import dev.slne.data.core.pusher.CorePusherModule;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.pusher.event.DiscordPusherEvent;

import java.time.LocalDateTime;

public class DiscordDataSource implements DataSource {

    private static DiscordDataSource instance;
    private DiscordDataInstance dataInstance;
    private DataApi dataApi;

    /**
     * Returns the instance.
     *
     * @return the instance
     */
    public static DiscordDataSource getInstance() {
        return instance;
    }

    @Override
    @SuppressWarnings({ "java:S2696", "java:S2440" })
    public void onLoad() {
        instance = this;

        dataInstance = new DiscordDataInstance();
        dataApi = new DataApi(dataInstance);

        dataInstance.getDataModuleLoader().registerModule(new CorePusherModule() {
            @Override
            public void callPusherEvent(String channelName, String eventName, String userId,
                                        PusherPacket pusherPacket) {
                DiscordBot.getInstance().getListenerManager().broadcastEvent(new DiscordPusherEvent(pusherPacket,
                        LocalDateTime.now(), channelName, eventName, userId, true));
            }
        });
        dataInstance.getDataModuleLoader().loadModules();
    }

    @Override
    public void onEnable() {
        dataInstance.getDataModuleLoader().enableModules();
    }

    @Override
    public void onDisable() {
        dataInstance.getDataModuleLoader().disableModules();
    }

    /**
     * Returns the data instance.
     *
     * @return the data instance
     */
    public DiscordDataInstance getDataInstance() {
        return dataInstance;
    }

    /**
     * Returns the data api.
     *
     * @return the data api
     */
    public DataApi getDataApi() {
        return dataApi;
    }

}
