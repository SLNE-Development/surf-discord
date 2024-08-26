package dev.slne.discord.spring.service.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.spring.feign.client.WhitelistClient;
import dev.slne.discord.spring.feign.client.WhitelistClient.WhitelistCheckPostRequest;
import dev.slne.discord.spring.feign.dto.WhitelistDTO;
import feign.FeignException;
import java.time.Instant;
import java.time.chrono.JapaneseDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * The type WhitelistDTO service.
 */
@Service
public class WhitelistService {

  private final WhitelistClient whitelistClient;
  private final JDA jda;

  @Autowired
  public WhitelistService(WhitelistClient whitelistClient, JDA jda) {
    this.whitelistClient = whitelistClient;
    this.jda = jda;
  }

  /**
   * Update whitelist completable future.
   *
   * @param whitelist the whitelist
   * @return the completable future
   */
  @Async
  public CompletableFuture<@Nullable WhitelistDTO> updateWhitelist(WhitelistDTO whitelist) {
    try {
      return CompletableFuture.completedFuture(
          whitelistClient.updateWhitelist(whitelist.getUuid(), whitelist)
      );
    } catch (FeignException.NotFound e) {
      return CompletableFuture.completedFuture(null);
    }
  }

  /**
   * Add whitelist completable future.
   *
   * @param whitelist the whitelist
   * @return the completable future
   */
  @Async
  public CompletableFuture<@Nullable WhitelistDTO> addWhitelist(WhitelistDTO whitelist) {
    try {
      return CompletableFuture.completedFuture(whitelistClient.addWhitelist(whitelist));
    } catch (FeignException e) {
      return CompletableFuture.completedFuture(null);
    }
  }

  /**
   * Gets whitelist by discord id.
   *
   * @param discordId the discord id
   * @return the whitelist by discord id
   */
  @Async
  public CompletableFuture<WhitelistDTO> getWhitelistByDiscordId(String discordId) {
    try {
      return CompletableFuture.completedFuture(whitelistClient.getWhitelistByDiscordId(discordId));
    } catch (FeignException.NotFound e) {
      return CompletableFuture.completedFuture(null);
    }
  }

  /**
   * Check whitelists completable future.
   *
   * @param uuid       the uuid
   * @param discordId  the discord id
   * @param twitchLink the twitch link
   * @return the completable future
   */
  @Async
  public CompletableFuture<@NotNull List<WhitelistDTO>> checkWhitelists(UUID uuid, String discordId,
      String twitchLink) {
    try {
      return CompletableFuture.completedFuture(
          whitelistClient.checkWhitelists(new WhitelistCheckPostRequest(
              uuid,
              discordId,
              twitchLink
          ))
      );
    } catch (FeignException e) {
      return CompletableFuture.completedFuture(List.of());
    }
  }

  @Async
  public CompletableFuture<Boolean> isWhitelisted(UUID uuid, String discordId, String twitchLink) {
    try {
      return CompletableFuture.completedFuture(checkWhitelists(uuid, discordId, twitchLink).join().isEmpty());
    } catch (FeignException e) {
      return CompletableFuture.completedFuture(false);
    }
  }
}
