package dev.slne.discord;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.slne.discord.datasource.DiscordDataSource;

public class Launcher {

    private DiscordBot discordBot;
    private DiscordDataSource dataSource;

    private static Random random;

    /**
     * Constructor for the launcher
     */
    @SuppressWarnings("java:S3010")
    public Launcher() {
        dataSource = new DiscordDataSource();
        discordBot = new DiscordBot();

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
    public static final Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * @return the random
     */
    public static Random getRandom() {
        return random;
    }

}
