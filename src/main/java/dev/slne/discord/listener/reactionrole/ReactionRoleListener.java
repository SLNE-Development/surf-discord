package dev.slne.discord.listener.reactionrole;

import dev.slne.discord.annotation.DiscordListener;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.config.discord.ReactionRoleConfig;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Reaction role listener.
 */
@DiscordListener
public class ReactionRoleListener extends ListenerAdapter {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("ReactionRoleListener");

  @Override
  public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
    handleReaction(event, true);
  }

  @Override
  public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
    handleReaction(event, false);
  }

  @Override
  public void onMessageReactionRemoveAll(@Nonnull MessageReactionRemoveAllEvent event) {
    removeAllReactionRoles(event.getGuild());
  }

  @Override
  public void onMessageReactionRemoveEmoji(@Nonnull MessageReactionRemoveEmojiEvent event) {
    if (isRelevantReaction(event.getGuild(), event.getEmoji())) {
      removeAllReactionRoles(event.getGuild());
    }
  }

  @Async
  protected void handleReaction(@NotNull GenericMessageReactionEvent event, boolean addRole) {
    final ReactionRoleConfig config = getReactionRoleConfig(event.getGuild());
    if (config == null || !isRelevantReaction(event, config)) {
      return;
    }

    try {
      final User user = event.retrieveUser().complete();
      final Member member = event.getGuild().retrieveMember(user).complete();
      final Role role = config.getRole();

      if (member == null || role == null) {
        return;
      }

      if (addRole) {
        if (!member.getRoles().contains(role)) {
          event.getGuild().addRoleToMember(member, role).complete();
        }
      } else {
        if (member.getRoles().contains(role)) {
          event.getGuild().removeRoleFromMember(member, role).complete();
        }
      }
    } catch (ErrorResponseException e) {
      if (e.getErrorCode() != 10007) {
        LOGGER.error("Error while handling reaction role", e);
      }
    }
  }

  @Async
  protected void removeAllReactionRoles(Guild guild) {
    final ReactionRoleConfig config = getReactionRoleConfig(guild);
    if (config == null || config.getRole() == null) {
      return;
    }

    guild.findMembersWithRoles(config.getRole()).onSuccess(members ->
        members.stream()
            .filter(Objects::nonNull)
            .forEach(member -> guild.removeRoleFromMember(member, config.getRole()).queue())
    );
  }

  @Blocking
  protected boolean isRelevantReaction(
      @NotNull GenericMessageReactionEvent event,
      @NotNull ReactionRoleConfig config
  ) {
    final RestAction<Message> messageRest = config.getMessageRest();
    if (messageRest == null) {
      return false;
    }

    final Message reactionMessage = messageRest.complete();
    final Message eventMessage = event.getChannel().retrieveMessageById(event.getMessageId())
        .complete();

    return eventMessage != null &&
           reactionMessage != null &&
           reactionMessage.getId().equals(eventMessage.getId()) &&
           Objects.equals(config.getReaction(), event.getEmoji().getName());
  }

  private boolean isRelevantReaction(Guild guild, Emoji emoji) {
    final ReactionRoleConfig config = getReactionRoleConfig(guild);

    return config != null && Objects.equals(config.getReaction(), emoji.getName());
  }

  private @Nullable ReactionRoleConfig getReactionRoleConfig(@NotNull Guild guild) {
    final GuildConfig guildConfig = GuildConfig.getConfig(guild.getId());

    return (guildConfig != null) ? guildConfig.getReactionRole() : null;
  }
}
