package dev.slne.discord;

import dev.slne.discord.datasource.DiscordDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Launcher {

    private static Random random;
    private final DiscordBot discordBot;
    private final DiscordDataSource dataSource;

    /**
     * Constructor for the launcher
     */
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

}
