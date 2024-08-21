package dev.slne.discord;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.spring.SurfSpringApplication;
import dev.slne.discord.datasource.DiscordDataInstance;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The type Discord spring application.
 */
@SurfSpringApplication(scanBasePackages = DiscordSpringApplication.BASE_PACKAGE, scanFeignBasePackages = DiscordSpringApplication.BASE_PACKAGE)
public class DiscordSpringApplication {
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

}
