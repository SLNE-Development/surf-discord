package dev.slne.discord.listener.reactionrole;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.reactionrole.ReactionRoleConfig;
import dev.slne.discord.listener.Listener;
import dev.slne.discord.listener.event.EventHandler;
import dev.slne.discord.listener.event.events.BotStartEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

public class ReactionRoleListener extends ListenerAdapter implements Listener {

    @EventHandler
    @SuppressWarnings({ "java:S3776", "java:S135" })
    public void onBotStart(BotStartEvent event) {
        for (DiscordGuild discordGuild : DiscordGuilds.getGuilds()) {
            ReactionRoleConfig reactionRoleConfig = discordGuild.getReactionRoleConfig();

            if (reactionRoleConfig == null) {
                continue;
            }

            Optional<RestAction<Message>> messageOptional = reactionRoleConfig.getMessageRest();

            if (messageOptional.isEmpty()) {
                continue;
            }

            RestAction<Message> messageRest = messageOptional.get();

            if (messageRest == null) {
                continue;
            }

            messageRest.queue(message -> {
                if (message == null) {
                    return;
                }

                Emoji emoji = reactionRoleConfig.getEmoji();

                if (emoji == null) {
                    return;
                }

                Guild guild = message.getGuild();
                Channel channel = message.getChannel();

                if (guild == null || channel == null || !(channel instanceof TextChannel)) {
                    return;
                }

                List<MessageReaction> messageReactions = message.getReactions();

                for (MessageReaction messageReaction : messageReactions) {
                    Emoji reactionEmoji = messageReaction.getEmoji();

                    if (emoji.hashCode() != reactionEmoji.hashCode()) {
                        continue;
                    }

                    messageReaction.retrieveUsers().onSuccess(users -> {
                        for (User user : users) {
                            if (user == null) {
                                continue;
                            }

                            addReactionRole(user, guild, channel, message.getId(), messageReaction);
                        }
                    }).queue();
                }
            });
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        Channel channel = event.getChannel();
        String messageId = event.getMessageId();
        MessageReaction messageReaction = event.getReaction();

        addReactionRole(user, guild, channel, messageId, messageReaction);
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        Channel channel = event.getChannel();
        String messageId = event.getMessageId();
        MessageReaction messageReaction = event.getReaction();

        removeReactionRole(user, guild, channel, messageId, messageReaction);
    }

    @Override
    public void onMessageReactionRemoveAll(@Nonnull MessageReactionRemoveAllEvent event) {
        Guild guild = event.getGuild();
        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

        if (discordGuild == null) {
            return;
        }

        ReactionRoleConfig reactionRoleConfig = discordGuild.getReactionRoleConfig();

        if (reactionRoleConfig == null) {
            return;
        }

        Optional<Role> roleOptional = reactionRoleConfig.getRole();

        if (roleOptional.isEmpty()) {
            return;
        }

        Role role = roleOptional.get();

        if (role == null) {
            return;
        }

        guild.findMembersWithRoles(role).onSuccess(members -> {
            for (Member member : members) {
                if (member == null) {
                    continue;
                }

                guild.removeRoleFromMember(member, role).queue();
            }
        });
    }

    @Override
    public void onMessageReactionRemoveEmoji(@Nonnull MessageReactionRemoveEmojiEvent event) {
        Guild guild = event.getGuild();
        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

        Emoji emoji = event.getEmoji();
        String formatted = emoji.getName();

        if (discordGuild == null) {
            return;
        }

        ReactionRoleConfig reactionRoleConfig = discordGuild.getReactionRoleConfig();

        if (reactionRoleConfig == null) {
            return;
        }

        Optional<Role> roleOptional = reactionRoleConfig.getRole();

        if (roleOptional.isEmpty()) {
            return;
        }

        Role role = roleOptional.get();

        if (role == null) {
            return;
        }

        if (!reactionRoleConfig.reaction().equals(formatted)) {
            return;
        }

        guild.findMembersWithRoles(role).onSuccess(members -> {
            for (Member member : members) {
                if (member == null) {
                    continue;
                }

                guild.removeRoleFromMember(member, role).queue();
            }
        });
    }

    /**
     * Adds the reaction role to the user.
     *
     * @param user  The user.
     * @param guild The guild.
     */
    @SuppressWarnings("java:S3776")
    private void addReactionRole(User user, Guild guild, Channel channel, String messageId,
            MessageReaction messageReaction) {
        if (channel == null || messageId == null || guild == null || user == null
                || !(channel instanceof TextChannel) || messageReaction == null) {
            return;
        }

        Emoji emoji = messageReaction.getEmoji();

        TextChannel textChannel = (TextChannel) channel;
        textChannel.retrieveMessageById(messageId).queue(message -> {
            if (message == null) {
                return;
            }

            if (message.getAuthor() != guild.getSelfMember().getUser()) {
                return;
            }

            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

            if (discordGuild == null) {
                return;
            }

            ReactionRoleConfig reactionRoleConfig = discordGuild.getReactionRoleConfig();

            if (reactionRoleConfig == null) {
                return;
            }

            Optional<RestAction<Message>> messageOptional = reactionRoleConfig.getMessageRest();

            if (messageOptional.isEmpty()) {
                return;
            }

            RestAction<Message> messageRest = messageOptional.get();

            if (messageRest == null) {
                return;
            }

            messageRest.queue(reactionMessage -> {
                if (reactionMessage == null) {
                    return;
                }

                if (!reactionMessage.getId().equals(message.getId())) {
                    return;
                }

                if (reactionRoleConfig.getEmoji().hashCode() != emoji.hashCode()) {
                    return;
                }

                Optional<Role> roleOptional = reactionRoleConfig.getRole();

                if (roleOptional.isEmpty()) {
                    return;
                }

                Role role = roleOptional.get();

                if (role == null) {
                    return;
                }

                Member member = guild.getMember(user);

                if (member == null) {
                    return;
                }

                if (member.getRoles().contains(role)) {
                    return;
                }

                guild.addRoleToMember(member, role).queue();
            });
        });
    }

    /**
     * Removes the reaction role from the user.
     *
     * @param user  The user.
     * @param guild The guild.
     */
    @SuppressWarnings("java:S3776")
    private void removeReactionRole(User user, Guild guild, Channel channel, String messageId,
            MessageReaction messageReaction) {
        if (channel == null || messageId == null || guild == null || user == null
                || !(channel instanceof TextChannel) || messageReaction == null) {
            return;
        }

        Emoji emoji = messageReaction.getEmoji();

        TextChannel textChannel = (TextChannel) channel;
        textChannel.retrieveMessageById(messageId).queue(message -> {
            if (message == null) {
                return;
            }

            if (message.getAuthor() != guild.getSelfMember().getUser()) {
                return;
            }

            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

            if (discordGuild == null) {
                return;
            }

            ReactionRoleConfig reactionRoleConfig = discordGuild.getReactionRoleConfig();

            if (reactionRoleConfig == null) {
                return;
            }

            Optional<RestAction<Message>> messageOptional = reactionRoleConfig.getMessageRest();

            if (messageOptional.isEmpty()) {
                return;
            }

            RestAction<Message> messageRest = messageOptional.get();

            if (messageRest == null) {
                return;
            }

            messageRest.queue(reactionMessage -> {
                if (reactionMessage == null) {
                    return;
                }

                if (!reactionMessage.getId().equals(message.getId())) {
                    return;
                }

                if (reactionRoleConfig.getEmoji().hashCode() != emoji.hashCode()) {
                    return;
                }

                Optional<Role> roleOptional = reactionRoleConfig.getRole();

                if (roleOptional.isEmpty()) {
                    return;
                }

                Role role = roleOptional.get();

                if (role == null) {
                    return;
                }

                Member member = guild.getMember(user);

                if (member == null) {
                    return;
                }

                if (!member.getRoles().contains(role)) {
                    return;
                }

                guild.removeRoleFromMember(member, role).queue();
            });
        });
    }

}
