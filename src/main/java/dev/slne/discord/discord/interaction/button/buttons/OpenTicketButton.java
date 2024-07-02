package dev.slne.discord.discord.interaction.button.buttons;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.button.DiscordButton;
import dev.slne.discord.discord.interaction.select.DiscordSelectMenu;
import dev.slne.discord.discord.interaction.select.menus.TicketsMenu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.awt.Color;

/**
 * The type Open ticket button.
 */
public class OpenTicketButton extends DiscordButton {

	/**
	 * Instantiates a new Open ticket button.
	 */
	public OpenTicketButton() {
		super("open-ticket", "Ticket öffnen", Emoji.fromUnicode("🎫"), ButtonStyle.SUCCESS);
	}

	@Override
	public void onClick(ButtonInteraction interaction) {
		DiscordSelectMenu selectMenu = new TicketsMenu(interaction.getId());
		DiscordBot.getInstance().getSelectMenuManager().addMenu(selectMenu);

		sendEmbed(selectMenu.build(), interaction);
	}

	/**
	 * Send the embeds
	 *
	 * @param menu        the menu
	 * @param interaction the interaction
	 */
	private void sendEmbed(SelectMenu menu, ButtonInteraction interaction) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setThumbnail("https://cdn.icon-icons.com/icons2/522/PNG/512/ticket_icon-icons.com_52351.png");
		builder.setTitle("Ticket Typ auswählen");
		builder.setDescription("Bitte wähle ein Ticket aus, welches du öffnen möchtest.");
		builder.setColor(Color.CYAN);

		interaction.reply("").setActionRow(menu).setEmbeds(builder.build()).setEphemeral(true).queue();
	}
}
