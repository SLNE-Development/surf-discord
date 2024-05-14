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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordBot {

	private static String botToken;

	private static DiscordBot instance;
	private JDA jda;

	private DiscordRoleManager roleManager;
	private ListenerManager listenerManager;
	private DiscordModalManager modalManager;
	private DiscordCommandManager commandManager;
	private TicketManager ticketManager;
	private DiscordButtonManager buttonManager;

	/**
	 * Gets the instance of the discord bot.
	 *
	 * @return the instance
	 */
	public static DiscordBot getInstance() {
		return instance;
	}

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

	/**
	 * Gets the command manager.
	 *
	 * @return the command manager
	 */
	public DiscordCommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * Gets the listener manager.
	 *
	 * @return the listener manager
	 */
	@SuppressWarnings("unused")
	public ListenerManager getListenerManager() {
		return listenerManager;
	}

	/**
	 * Gets the modal manager.
	 *
	 * @return the modal manager
	 */
	public DiscordModalManager getModalManager() {
		return modalManager;
	}

	/**
	 * Gets the ticket manager.
	 *
	 * @return the ticket manager
	 */
	public TicketManager getTicketManager() {
		return ticketManager;
	}

	/**
	 * Gets the jda.
	 *
	 * @return the jda
	 */
	public JDA getJda() {
		return jda;
	}

	/**
	 * @return the buttonManager
	 */
	public DiscordButtonManager getButtonManager() {
		return buttonManager;
	}

	/**
	 * @return the roleManager
	 */
	public DiscordRoleManager getRoleManager() {
		return roleManager;
	}
}
