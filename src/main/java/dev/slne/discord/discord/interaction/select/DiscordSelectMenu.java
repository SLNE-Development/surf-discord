package dev.slne.discord.discord.interaction.select;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.List;

/**
 * The type Discord select menu.
 */
@Getter
@AllArgsConstructor
public abstract class DiscordSelectMenu {

	private final String id;
	private final String placeholder;
	private final List<DiscordSelectMenuOption> options;

	private int minValues = 1;
	private int maxValues = 1;

	/**
	 * Build select menu.
	 *
	 * @return the select menu
	 */
	public SelectMenu build() {
		StringSelectMenu.Builder builder = StringSelectMenu.create(id).setPlaceholder(placeholder);

		for (DiscordSelectMenuOption option : options) {
			builder.addOption(option.getLabel(), option.getValue(), option.getDescription(), option.getEmoji());
		}



		return builder.build();
	}

	/**
	 * On select.
	 *
	 * @param interaction     the interaction
	 * @param selectedOptions the selected options
	 */
	public abstract void onSelect(StringSelectInteraction interaction, List<DiscordSelectMenuOption> selectedOptions);

	/**
	 * Gets option.
	 *
	 * @param value the value
	 *
	 * @return the option
	 */
	public DiscordSelectMenuOption getOptionByValue(String value) {
		return options.stream().filter(option -> option.getValue().equals(value)).findFirst().orElse(null);
	}

	/**
	 * The type Discord select menu option.
	 */
	@Getter
	@AllArgsConstructor
	public static class DiscordSelectMenuOption {
		private String label;
		private String value;
		private String description;
		private Emoji emoji;

		/**
		 * Of discord select menu option.
		 *
		 * @param label       the label
		 * @param value       the value
		 * @param description the description
		 * @param emoji       the emoji
		 *
		 * @return the discord select menu option
		 */
		public static DiscordSelectMenuOption of(String label, String value, String description, Emoji emoji) {
			return new DiscordSelectMenuOption(label, value, description, emoji);
		}
	}
}

