package dev.slne.discord.spring.service.punishment;

import dev.slne.discord.spring.feign.client.PunishmentClient;
import dev.slne.discord.spring.feign.dto.PunishmentBanDTO;
import feign.FeignException;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PunishmentService {

  private final PunishmentClient punishmentClient;

  @Autowired
  public PunishmentService(PunishmentClient punishmentClient) {
    this.punishmentClient = punishmentClient;
  }

  /**
   * Gets ban by punishment id.
   *
   * @param punishmentId the punishment id
   * @return the ban by punishment id
   */
  @Async
  public CompletableFuture<@Nullable PunishmentBanDTO> getBanByPunishmentId(String punishmentId) {
    try {
      return CompletableFuture.completedFuture(punishmentClient.getBanByPunishmentId(punishmentId));
    } catch (FeignException.NotFound ignored) {
      return CompletableFuture.completedFuture(null);
    }
  }

  /**
   * Is valid punishment id completable future.
   *
   * @param punishmentId the punishment id
   * @return the completable future
   */
  @Async
  public CompletableFuture<Boolean> isValidPunishmentId(String punishmentId) {
    return CompletableFuture.completedFuture(getBanByPunishmentId(punishmentId).join() != null);
  }
}
