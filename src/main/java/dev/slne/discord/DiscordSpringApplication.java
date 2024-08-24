package dev.slne.discord;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.spring.SurfSpringApplication;
import dev.slne.discord.datasource.DiscordDataInstance;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.AsyncConfigurer;

/**
 * The type Discord spring application.
 */
@SurfSpringApplication(scanBasePackages = DiscordSpringApplication.BASE_PACKAGE, scanFeignBasePackages = "dev.slne.discord.spring.feign.client")
public class DiscordSpringApplication implements AsyncConfigurer {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("DiscordBotApplication");
  public static final String BASE_PACKAGE = "dev.slne.discord";

  /**
   * Run.
   *
   * @return the configurable application context
   */
  public static ConfigurableApplicationContext run() {
    return DataApi.run(
        DiscordSpringApplication.class,
        DiscordDataInstance.class.getClassLoader()
    );
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (throwable, method, params) -> {
      LOGGER.error("""
               Exception message - {}
               Method name - {}
               ParameterValues - {}
          """,
          throwable.getMessage(),
          method.getName(),
          ArrayUtils.toString(params)
      );
    };
  }
}
