package dev.slne.discord.discord.guild.reactionrole;

import dev.slne.discord.DiscordBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.RestAction;

public record ReactionRoleConfig(String messageId, String channelId, String reaction, String roleId) {

    /**
     * Returns the message rest.
     *
     * @return the message rest.
     */
    public RestAction<Message> getMessageRest() {
        if (this.messageId() == null || this.channelId() == null) {
            return null;
        }

        TextChannel channel = this.getTextChannel();

        if (channel == null) {
            return null;
        }

        return channel.retrieveMessageById(this.messageId() + "");
    }

    /**
     * Returns the text channel.
     *
     * @return the text channel.
     */
    public TextChannel getTextChannel() {
        if (this.channelId() == null) {
            return null;
        }

        Channel channel = DiscordBot.getInstance().getJda().getTextChannelById(this.channelId() + "");

        if (channel == null || !channel.getType().isMessage()) {
            return null;
        }

        return (TextChannel) channel;
    }

    /**
     * Returns the role.
     *
     * @return the role.
     */
    public Role getRole() {
        if (this.roleId() == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().getRoleById(this.roleId() + "");
    }

    /**
     * Returns the emoji.
     *
     * @return the emoji.
     */
    public Emoji getEmoji() {
        if (this.reaction() == null) {
            return null;
        }

        return Emoji.fromFormatted(this.reaction() + "");
    }

}
