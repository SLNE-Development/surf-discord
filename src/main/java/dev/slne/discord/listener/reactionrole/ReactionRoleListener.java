package dev.slne.discord.listener.reactionrole;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.reactionrole.ReactionRoleConfig;
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
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.Nonnull;

public class ReactionRoleListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        Guild guild = event.getGuild();

        event.retrieveUser().queue(user -> {
            Channel channel = event.getChannel();
            String messageId = event.getMessageId();
            MessageReaction messageReaction = event.getReaction();

            addReactionRole(user, guild, channel, messageId, messageReaction);
        });
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        Guild guild = event.getGuild();

        event.retrieveUser().queue(user -> {
            Channel channel = event.getChannel();
            String messageId = event.getMessageId();
            MessageReaction messageReaction = event.getReaction();

            removeReactionRole(user, guild, channel, messageId, messageReaction);
        });
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

        Role role = reactionRoleConfig.getRole();

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

        Role role = reactionRoleConfig.getRole();

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
                || !(channel instanceof TextChannel textChannel) || messageReaction == null) {
            return;
        }

        Emoji emoji = messageReaction.getEmoji();

        textChannel.retrieveMessageById(messageId).queue(message -> {
            if (message == null) {
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

            RestAction<Message> messageRest = reactionRoleConfig.getMessageRest();

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

                Role role = reactionRoleConfig.getRole();

                if (role == null) {
                    return;
                }

                guild.retrieveMember(user).queue(member -> {
                    if (member == null) {
                        return;
                    }

                    if (member.getRoles().contains(role)) {
                        return;
                    }

                    guild.addRoleToMember(member, role).queue();
                }, throwable -> {
                    if (throwable instanceof ErrorResponseException errorResponseException) {

                        if (errorResponseException.getErrorCode() == 10007) {
                            return;
                        }
                    }

                    DataApi.getDataInstance().logError(getClass(), "Error while adding reaction role", throwable);
                });
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
        if (messageId == null || guild == null || user == null
                || !(channel instanceof TextChannel textChannel) || messageReaction == null) {
            return;
        }

        Emoji emoji = messageReaction.getEmoji();

        textChannel.retrieveMessageById(messageId).queue(message -> {
            if (message == null) {
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

            RestAction<Message> messageRest = reactionRoleConfig.getMessageRest();

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

                Role role = reactionRoleConfig.getRole();

                if (role == null) {
                    return;
                }

                guild.retrieveMember(user).queue(member -> {
                    if (member == null) {
                        return;
                    }

                    if (!member.getRoles().contains(role)) {
                        return;
                    }

                    guild.removeRoleFromMember(member, role).queue();
                }, throwable -> {
                    if (throwable instanceof ErrorResponseException errorResponseException) {

                        if (errorResponseException.getErrorCode() == 10007) {
                            return;
                        }
                    }

                    DataApi.getDataInstance().logError(getClass(), "Error while removing reaction role", throwable);
                });
            });
        });
    }

}
