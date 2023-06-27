package dev.slne.discord.discord.guild.reactionrole;

import java.util.Optional;

import dev.slne.discord.DiscordBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;

public record ReactionRoleConfig(String messageId, String channelId, String reaction, String roleId) {

    /**
     * Returns the message rest.
     *
     * @return the message rest.
     */
    @SuppressWarnings("null")
    public Optional<RestAction<Message>> getMessageRest() {
        if (this.messageId == null || this.channelId == null) {
            return Optional.empty();
        }

        Optional<TextChannel> textChannelOptional = this.getTextChannel();

        if (textChannelOptional.isEmpty()) {
            return Optional.empty();
        }

        TextChannel textChannel = textChannelOptional.get();

        return Optional.of(textChannel.retrieveMessageById(this.messageId()));
    }

    /**
     * Returns the text channel.
     *
     * @return the text channel.
     */
    @SuppressWarnings("null")
    public Optional<TextChannel> getTextChannel() {
        if (this.channelId == null) {
            return Optional.empty();
        }

        Channel channel = DiscordBot.getInstance().getJda().getTextChannelById(this.channelId());

        if (channel == null || !channel.getType().isMessage()) {
            return Optional.empty();
        }

        return Optional.of((TextChannel) channel);
    }

    /**
     * Returns the role.
     *
     * @return the role.
     */
    @SuppressWarnings("null")
    public Optional<Role> getRole() {
        if (this.roleId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(DiscordBot.getInstance().getJda().getRoleById(this.roleId()));
    }

}
