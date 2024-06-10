package dev.slne.discord;

import dev.slne.data.api.DataApi;
import dev.slne.discord.config.BotConfig;
import dev.slne.discord.config.ConfigUtil;
import dev.slne.discord.discord.guild.role.DiscordRoleManager;
import dev.slne.discord.discord.interaction.button.DiscordButtonManager;
import dev.slne.discord.discord.interaction.command.DiscordCommandManager;
import dev.slne.discord.discord.interaction.modal.DiscordModalManager;
import dev.slne.discord.discord.settings.GatewayIntents;
import dev.slne.discord.listener.ListenerManager;
import dev.slne.discord.ticket.TicketManager;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

/**
 * The type Discord bot.
 */
@Getter
public class DiscordBot {

	private static String botToken;

	@Getter
	private static DiscordBot instance;

	private JDA jda;

	private ListenerManager listenerManager;
	private TicketManager ticketManager;

	private DiscordRoleManager roleManager;
	private DiscordModalManager modalManager;
	private DiscordCommandManager commandManager;
	private DiscordButtonManager buttonManager;
	
	/**
	 * Called when the bot is loaded.
	 */
	public void onLoad() {
		instance = this;

		BotConfig botConfig = ConfigUtil.getConfig();
		botToken = botConfig.discordBotConfig().botToken();

		JDABuilder builder = JDABuilder.createDefault(botToken);

		builder.setAutoReconnect(true);
		builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.MEMBER_OVERRIDES,
							CacheFlag.ONLINE_STATUS, CacheFlag.ROLE_TAGS, CacheFlag.STICKER,
							CacheFlag.SCHEDULED_EVENTS
		);
		builder.disableCache(CacheFlag.VOICE_STATE);
		builder.setEnabledIntents(GatewayIntents.getGatewayIntents());
		builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

		this.jda = builder.build();
		try {
			this.jda.awaitReady();
		} catch (InterruptedException exception) {
			DataApi.getDataInstance().logError(getClass(), "Failed to await ready.", exception);
		}

		roleManager = new DiscordRoleManager();

		commandManager = new DiscordCommandManager();
		listenerManager = new ListenerManager();
		modalManager = new DiscordModalManager();
		ticketManager = new TicketManager();

		listenerManager.registerDiscordListeners();
		listenerManager.registerListeners();

		for (Guild guild : jda.getGuilds()) {
			DiscordBot.getInstance().getCommandManager().registerToGuild(guild);
		}

		DiscordBot.getInstance().getTicketManager().fetchActiveTickets();
		DataApi.getDataInstance().logInfo(getClass(), "Discord Bot is ready");
	}

	/**
	 * Called when the bot is enabled.
	 */
	public void onEnable() {
		if (botToken == null) {
			DataApi.getDataInstance()
				   .logError(getClass(), "Bot token is null. Please check your bot-connection.json file.");
			System.exit(1000);
			return;
		}

		listenerManager.registerListenersToJda(this.jda);

		buttonManager = new DiscordButtonManager();
	}

	/**
	 * Called when the bot is disabled.
	 */
	public void onDisable() {
		// Currently empty
	}

}
