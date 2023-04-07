package dev.slne.discord;

import dev.slne.discord.datasource.DiscordDataSource;

public class Launcher {

    private DiscordBot discordBot;
    private DiscordDataSource dataSource;

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
        discordBot.onEnable();
    }

    public void onDisable() {
        dataSource.onDisable();
        discordBot.onDisable();
    }

    public DiscordDataSource getDataSource() {
        return dataSource;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

}
