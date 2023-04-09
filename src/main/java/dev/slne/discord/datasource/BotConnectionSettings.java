package dev.slne.discord.datasource;

public class BotConnectionSettings {

    private String botToken;

    public BotConnectionSettings() {

    }

    public BotConnectionSettings(String botToken) {
        this.botToken = botToken;
    }

    public String getBotToken() {
        return botToken;
    }

}
