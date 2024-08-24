package dev.slne.discord.listener.interaction.menu;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.select.DiscordSelectMenu;
import dev.slne.discord.discord.interaction.select.DiscordSelectMenuManager;
import dev.slne.discord.spring.annotation.DiscordListener;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * The type Discord select menu listener.
 */
@DiscordListener
public class DiscordSelectMenuListener extends ListenerAdapter {

	private final DiscordSelectMenuManager discordSelectMenuManager;

	/**
	 * Instantiates a new Discord select menu listener.
	 */
	public DiscordSelectMenuListener() {
		this.discordSelectMenuManager = DiscordBot.getInstance().getSelectMenuManager();
	}

	@Override
	public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
		DiscordSelectMenu menu = discordSelectMenuManager.getMenu(event.getComponentId());

		if (menu != null) {
			menu.onSelect(event.getInteraction(), event.getValues().stream().map(menu::getOptionByValue).toList());
		}
//		else {
//			event.reply("Die Interaktion ist abgelaufen, oder konnte nicht gefunden werden!").setEphemeral(true)
//				 .queue();
//		}
	}

}
