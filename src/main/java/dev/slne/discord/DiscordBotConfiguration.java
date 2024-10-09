package dev.slne.discord;

import dev.slne.discord.config.BotConfig;
import dev.slne.discord.discord.settings.GatewayIntents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class DiscordBotConfiguration {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("DiscordBot");

  @Bean
  @Scope("singleton")
  public JDA jda() {
    final BotConfig botConfig = BotConfig.getConfig();
    final String botToken = botConfig.getDiscordBotConfig().getBotToken();

    if (botToken == null) {
      LOGGER.error("Bot token is null. Exiting...");
      ExitCodes.BOT_TOKEN_NOT_SET.exit();
    }

    final JDA jda = JDABuilder.createDefault(botToken)
        .setAutoReconnect(true)
        .enableCache(
            CacheFlag.ACTIVITY,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.EMOJI,
            CacheFlag.MEMBER_OVERRIDES,
            CacheFlag.ONLINE_STATUS,
            CacheFlag.ROLE_TAGS,
            CacheFlag.STICKER,
            CacheFlag.SCHEDULED_EVENTS
        )
        .disableCache(CacheFlag.VOICE_STATE)
        .setEnabledIntents(GatewayIntents.getGatewayIntents())
        .setStatus(OnlineStatus.DO_NOT_DISTURB)
        .build();

    try {
      jda.awaitReady();
    } catch (InterruptedException e) {
      LOGGER.error("Failed to start JDA. Existing...", e);
      ExitCodes.FAILED_AWAIT_READY_JDA.exit();
    }

    return jda;
  }
}
