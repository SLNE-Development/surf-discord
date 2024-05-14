package dev.slne.discord;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.spring.SurfSpringApplication;
import dev.slne.discord.datasource.DiscordDataInstance;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The type Discord spring application.
 */
@SurfSpringApplication(scanBasePackages = "dev.slne.discord", scanFeignBasePackages = "dev.slne.discord")
public class DiscordSpringApplication {

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
