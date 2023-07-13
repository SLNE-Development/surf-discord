package dev.slne.discord.datasource;

import dev.slne.data.core.DataSource;
import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.pusher.PusherModule;

public class DiscordDataSource implements DataSource {

    private static DiscordDataSource instance;
    private DiscordDataInstance dataInstance;
    private DataApi dataApi;

    @Override
    @SuppressWarnings({ "java:S2696", "java:S2440" })
    public void onLoad() {
        instance = this;

        dataInstance = new DiscordDataInstance();
        dataApi = new DataApi(dataInstance);

        dataInstance.getDataModuleLoader().registerModule(new PusherModule());
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
     * Returns the instance.
     *
     * @return the instance
     */
    public static DiscordDataSource getInstance() {
        return instance;
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
