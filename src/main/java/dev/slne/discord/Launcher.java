package dev.slne.discord;

import dev.slne.data.api.DataApi;
import dev.slne.discord.datasource.DiscordDataInstance;
import dev.slne.discord.instance.SurfDiscordApiAccess;
import dev.slne.discord.instance.SurfDiscordApiImpl;
import lombok.Getter;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Random;

/**
 * The type Launcher.
 */
@Getter
public class Launcher {

	@Getter
	private static Random random;

	@Getter
	private static ConfigurableApplicationContext context;
	private final DiscordBot discordBot;

	private DiscordDataInstance dataInstance;
	private SurfDiscordApiImpl surfDiscordApi;

	/**
	 * Constructor for the launcher
	 */
	public Launcher() {
		discordBot = new DiscordBot();

		random = new Random();
	}

	/**
	 * Main method
	 *
	 * @param args The arguments
	 */
	public static void main(String[] args) {
		Launcher launcher = new Launcher();

		launcher.onLoad();
		launcher.onEnable();
	}

	/**
	 * Method called when the launcher is loaded
	 */
	public void onLoad() {
		surfDiscordApi = new SurfDiscordApiImpl();
		SurfDiscordApiAccess.setInstance(surfDiscordApi);
		
		dataInstance = new DiscordDataInstance();
		new DataApi(dataInstance);

		context = DiscordSpringApplication.run();

		discordBot.onLoad();
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
