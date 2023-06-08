package dev.slne.discord.discord.guild;

public class DiscordGuild {

    private String guildId;
    private String categoryId;
    private String discordTranscriptId;
    private String serverTranscriptId;

    /**
     * Construct a new DiscordGuild.
     *
     * @param guildId             The guild id.
     * @param categoryId          The category id .
     * @param discordTranscriptId The discord transcript id.
     * @param serverTranscriptId  The server transcript id.
     */
    public DiscordGuild(String guildId, String categoryId, String discordTranscriptId, String serverTranscriptId) {
        this.guildId = guildId;
        this.categoryId = categoryId;
        this.discordTranscriptId = discordTranscriptId;
        this.serverTranscriptId = serverTranscriptId;
    }

    /**
     * Returns the guild id.
     *
     * @return The guild id.
     */
    public String getGuildId() {
        return guildId;
    }

    /**
     * Returns the category id.
     *
     * @return The category id.
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Returns the discord transcript id.
     *
     * @return The discord transcript id.
     */
    public String getDiscordTranscriptId() {
        return discordTranscriptId;
    }

    /**
     * Returns the server transcript id.
     *
     * @return The server transcript id.
     */
    public String getServerTranscriptId() {
        return serverTranscriptId;
    }

}
