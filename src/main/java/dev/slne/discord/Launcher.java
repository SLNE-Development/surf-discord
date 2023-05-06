package dev.slne.discord;

import java.lang.reflect.Method;

import com.mysql.cj.log.Slf4JLogger;

import dev.slne.data.core.database.worker.ConnectionWorkers;
import dev.slne.discord.datasource.DiscordDataSource;

public class Launcher {

    private DiscordBot discordBot;
    private DiscordDataSource dataSource;

    private static final Slf4JLogger LOGGER = new Slf4JLogger("Launcher");

    public Launcher() {
        discordBot = new DiscordBot();
        dataSource = new DiscordDataSource();
    }

    public static void main(String[] args) {
        Launcher launcher = new Launcher();

        launcher.onLoad();
        launcher.onEnable();
    }

    public void onLoad() {
        dataSource.onLoad();
        discordBot.onLoad();
    }

    public void onEnable() {
        dataSource.onEnable();
        ConnectionWorkers.asyncVoid(() -> reinitializeDatabaseRegistry());

        discordBot.onEnable();
    }

    public void onDisable() {
        dataSource.onDisable();
        discordBot.onDisable();
    }

    public void reinitializeDatabaseRegistry() {
        try {
            Class<?> registryClass = Class.forName("org.javalite.activejdbc.Registry");

            Object registryInstanceObject = registryClass.getField("INSTANCE").get(registryClass);
            Method registryInstanceInitMethod = registryClass.getDeclaredMethod("init", String.class);

            registryInstanceInitMethod.setAccessible(true);
            registryInstanceInitMethod.invoke(registryInstanceObject, "default");
            registryInstanceInitMethod.setAccessible(false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public DiscordDataSource getDataSource() {
        return dataSource;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public static final Slf4JLogger getLogger() {
        return LOGGER;
    }

}
