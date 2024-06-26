package dev.slne.discord.listener.interaction.button;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.button.DiscordButton;
import dev.slne.discord.discord.interaction.button.DiscordButtonManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * The type Discord button listener.
 */
public class DiscordButtonListener extends ListenerAdapter {

	private final DiscordButtonManager buttonManager;

	/**
	 * Instantiates a new Discord button listener.
	 */
	public DiscordButtonListener() {
		this.buttonManager = DiscordBot.getInstance().getButtonManager();
	}

	@Override
	public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
		DiscordButton button = buttonManager.getButton(event.getButton().getId());

		if (button != null) {
			button.onClick(event);
		}
	}

}
