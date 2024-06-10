package dev.slne.discord.discord.interaction.button;

import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The type Discord button.
 */
@Getter
public abstract class DiscordButton {

	@Nonnull
	private final String id;
	@Nonnull
	private final String label;
	@Nullable
	private final Emoji emoji;
	@Nonnull
	private final ButtonStyle style;

	/**
	 * The DiscordButton
	 *
	 * @param id    the id of the button
	 * @param label the label of the button
	 * @param emoji the icon of the button
	 * @param style the style
	 */
	protected DiscordButton(
			@Nonnull String id, @Nonnull String label, @Nullable Emoji emoji,
			@Nonnull ButtonStyle style
	) {
		this.id = id;
		this.label = label;
		this.emoji = emoji;
		this.style = style;
	}

	/**
	 * Forms the button
	 *
	 * @return the button
	 */
	public Button formDiscordButton() {
		return Button.of(style, id, label, emoji);
	}

	/**
	 * The action of the button
	 *
	 * @param interaction the interaction
	 */
	public abstract void onClick(ButtonInteraction interaction);

}
