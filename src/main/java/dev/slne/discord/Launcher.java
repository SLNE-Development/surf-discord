package dev.slne.discord;

import java.util.Random;

import com.mysql.cj.log.Slf4JLogger;

import dev.slne.discord.datasource.DiscordDataSource;

public class Launcher {

    private DiscordBot discordBot;
    private DiscordDataSource dataSource;

    private static Random random;
    private static final Slf4JLogger LOGGER = new Slf4JLogger("Launcher");

    /**
     * Constructor for the launcher
     */
    @SuppressWarnings("java:S3010")
    public Launcher() {
        discordBot = new DiscordBot();
        dataSource = new DiscordDataSource();

        random = new Random();
    }

    /**
     * Main method
     *
     * @param args The arguments
     */
    public static void main(String[] args) {
        Launcher launcher = new Launcher();

        launcher.onLoad();
        launcher.onEnable();
    }

    /**
     * Method called when the launcher is loaded
     */
    public void onLoad() {
        dataSource.onLoad();
        discordBot.onLoad();
    }

    /**
     * Method called when the launcher is enabled
     */
    public void onEnable() {
        dataSource.onEnable();
        discordBot.onEnable();
    }

    /**
     * Method called when the launcher is disabled
     */
    public void onDisable() {
        dataSource.onDisable();
        discordBot.onDisable();
    }

    /**
     * Retuns the data source
     *
     * @return The data source
     */
    public DiscordDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Returns the discord bot
     *
     * @return The discord bot
     */
    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    /**
     * Returns the logger
     *
     * @return The logger
     */
    public static final Slf4JLogger getLogger() {
        return LOGGER;
    }

    /**
     * @return the random
     */
    public static Random getRandom() {
        return random;
    }

}
