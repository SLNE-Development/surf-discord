package dev.slne.discord.discord.settings;

public class BotConnectionSettings {

    private String botToken;

    /**
     * Default constructor for BotConnectionSettings
     */
    public BotConnectionSettings() {

    }

    /**
     * Constructor for BotConnectionSettings
     *
     * @param botToken The bot token.
     */
    public BotConnectionSettings(String botToken) {
        this.botToken = botToken;
    }

    /**
     * Returns the bot token.
     *
     * @return The bot token.
     */
    public String getBotToken() {
        return botToken;
    }

}
