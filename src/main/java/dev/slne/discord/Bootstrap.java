package dev.slne.discord;

import dev.slne.data.api.DataApi;
import dev.slne.discord.datasource.DiscordDataInstance;
import dev.slne.discord.message.RawMessages;
import java.util.Random;
import lombok.Getter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The type Bootstrap.
 */
@Getter
public class Bootstrap {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("Bootstrap");
  @Getter
  private static Random random;

  @Getter
  private static ConfigurableApplicationContext context;
  private final DiscordBot discordBot;

  private DiscordDataInstance dataInstance;

  /**
   * Constructor for the launcher
   */
  public Bootstrap() {
    discordBot = new DiscordBot();

    random = new Random();
  }

  /**
   * Main method
   *
   * @param args The arguments
   */
  public static void main(String[] args) {
    Bootstrap bootstrap = new Bootstrap();

    LOGGER.info("Loading messages...");
    RawMessages.class.getClassLoader();

    bootstrap.onLoad();
    bootstrap.onEnable();
  }

  /**
   * Method called when the launcher is loaded
   */
  public void onLoad() {
    final long start = System.currentTimeMillis();
    dataInstance = new DiscordDataInstance();
    new DataApi(dataInstance);

    context = DiscordSpringApplication.run();

    discordBot.onLoad();
    final long end = System.currentTimeMillis();

    LOGGER.info(
        "Done (%.3fs)! Type 'help' for a list of commands.".formatted((end - start) / 1000.0));
  }

  /**
   * Method called when the launcher is enabled
   */
  public void onEnable() {
    discordBot.onEnable();
  }

  /**
   * Method called when the launcher is disabled
   */
  public void onDisable() {
    discordBot.onDisable();
  }

}
