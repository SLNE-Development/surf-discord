package dev.slne.discord.datasource;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.DataSource;

public class DiscordDataSource implements DataSource {

    private static DiscordDataSource instance;
    private DiscordDataInstance dataInstance;

    /**
     * Returns the instance.
     *
     * @return the instance
     */
    public static DiscordDataSource getInstance() {
        return instance;
    }

    @Override
    @SuppressWarnings("InstantiationOfUtilityClass")
    public void onLoad() {
        instance = this;

        dataInstance = new DiscordDataInstance();
        new DataApi(dataInstance);

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

}
