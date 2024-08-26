package dev.slne.discord.spring.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.message.EmbedColors;
import dev.slne.discord.spring.service.whitelist.WhitelistService;
import dev.slne.discord.util.TimeUtils;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The type WhitelistDTO.
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class WhitelistDTO {

  @JsonProperty("id")
  private long id;

  @JsonProperty("uuid")
  private @NonNull UUID uuid;

  @JsonProperty("minecraft_name")
  private String minecraftName;

  @JsonProperty("twitch_link")
  private String twitchLink;

  @JsonProperty("discord_id")
  private String discordId;

  @JsonProperty("added_by_id")
  private String addedById;

  @JsonProperty("added_by_name")
  private String addedByName;

  @JsonProperty("added_by_avatar_url")
  private String addedByAvatarUrl;

  @JsonProperty("blocked")
  @Setter
  private boolean blocked;

  @JsonProperty("created_at")
  private ZonedDateTime createdAt;

  public static WhitelistDTO createFrom(
      @NotNull UUID uuid,
      String minecraftName,
      String twitchLink,
      User user,
      User executor
  ) {
    final WhitelistDTOBuilder builder = WhitelistDTO.builder()
        .uuid(uuid)
        .minecraftName(minecraftName)
        .twitchLink(twitchLink);

    if (user != null) {
      builder.discordId(user.getId());
    }

    if (executor != null) {
      builder.addedById(executor.getId())
          .addedByName(executor.getName())
          .addedByAvatarUrl(executor.getAvatarUrl());
    }

    return builder.build();
  }

  /**
   * Returns if a {@link User} is whitelisted.
   *
   * @param user The {@link User}.
   * @return The {@link CompletableFuture}.
   */
  public static CompletableFuture<Boolean> isWhitelisted(User user) {
    return WhitelistService.INSTANCE.getWhitelistByDiscordId(user.getId())
        .thenApply(Objects::nonNull)
        .exceptionally(e -> false);
  }

  /**
   * Returns a {@link MessageEmbed} for a {@link WhitelistDTO}.
   *
   * @param whitelist The {@link WhitelistDTO}.
   * @return The {@link MessageEmbed}.
   */
  public static @Nonnull CompletableFuture<MessageEmbed> getWhitelistQueryEmbed(
      WhitelistDTO whitelist) {
    CompletableFuture<MessageEmbed> future = new CompletableFuture<>();

    EmbedBuilder builder = new EmbedBuilder();

    builder.setTitle("Whitelist Query");
    builder.setFooter("Whitelist Query",
        DiscordBot.getInstance().getJda().getSelfUser().getAvatarUrl());
    builder.setDescription("Whitelist Informationen");
    builder.setColor(EmbedColors.WL_QUERY);
    builder.setTimestamp(TimeUtils.berlinTimeProvider().getCurrentTime());

    DataApi.getNameByPlayerUuid(whitelist.getUuid()).thenAcceptAsync(name -> {
      UUID uuid = whitelist.getUuid();
      String twitchLink = whitelist.getTwitchLink();
      RestAction<User> discordUserRest = whitelist.getDiscordUser();
      RestAction<User> addedByRest = whitelist.getAddedBy();

      CompletableFuture<User> discordUserFuture = new CompletableFuture<>();
      CompletableFuture<User> addedByFuture = new CompletableFuture<>();

      if (discordUserRest != null) {
        discordUserFuture = discordUserRest.submit();
      } else {
        discordUserFuture.complete(null);
      }

      if (addedByRest != null) {
        addedByFuture = addedByRest.submit();
      } else {
        addedByFuture.complete(null);
      }

      final CompletableFuture<User> finaldiscordUserFuture = discordUserFuture;
      final CompletableFuture<User> finalAddedByFuture = addedByFuture;

      CompletableFuture.allOf(finaldiscordUserFuture, finalAddedByFuture).thenAccept(v -> {
        User discordUser = finaldiscordUserFuture.join();
        User addedBy = finalAddedByFuture.join();

        if (name != null) {
          builder.addField("Minecraft Name", name, true);
        }

        if (twitchLink != null) {
          builder.addField("Twitch Link", twitchLink, true);
        }

        if (discordUser != null) {
          builder.addField("Discord User", discordUser.getAsMention(), true);
        }

        if (addedBy != null) {
          builder.addField("Added By", addedBy.getAsMention(), true);
        }

        if (uuid != null) {
          builder.addField("UUID", uuid.toString(), false);
        }

        future.complete(builder.build());
      }).exceptionally(exception -> {
        future.completeExceptionally(exception);
        return null;
      });
    });

    return future;
  }

  /**
   * Gets added by.
   *
   * @return the addedBy
   */
  @JsonIgnore
  @Nullable
  public RestAction<User> getAddedBy() {
    if (addedById == null) {
      return null;
    }

    return DiscordBot.getInstance().getJda().retrieveUserById(addedById);
  }

  @JsonIgnore
  @Blocking
  @Nullable
  public User getAddedByNow() {
    final RestAction<User> restAction = getAddedBy();

    return restAction != null ? restAction.complete() : null;
  }

  /**
   * Gets discord user.
   *
   * @return the discordUser
   */
  @JsonIgnore
  @Nullable
  public RestAction<User> getDiscordUser() {
    if (discordId == null) {
      return null;
    }

    return DiscordBot.getInstance().getJda().retrieveUserById(discordId);
  }

  @JsonIgnore
  @Blocking
  @Nullable
  public User getDiscordUserNow() {
    final RestAction<User> restAction = getDiscordUser();

    return restAction != null ? restAction.complete() : null;
  }
}
