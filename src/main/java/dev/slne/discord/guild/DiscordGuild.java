package dev.slne.discord.guild;

public class DiscordGuild {

    private String guildId;
    private String categoryId;
    private String discordTranscriptId;
    private String serverTranscriptId;

    public DiscordGuild(String guildId, String categoryId, String discordTranscriptId, String serverTranscriptId) {
        this.guildId = guildId;
        this.categoryId = categoryId;
        this.discordTranscriptId = discordTranscriptId;
        this.serverTranscriptId = serverTranscriptId;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getDiscordTranscriptId() {
        return discordTranscriptId;
    }

    public String getServerTranscriptId() {
        return serverTranscriptId;
    }

}
