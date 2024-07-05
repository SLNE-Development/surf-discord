package dev.slne.discord.discord.interaction.command.commands.ticket;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.guild.permission.CommandPermission;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Ticket button command.
 */
public class TicketButtonCommand extends DiscordCommand {

	/**
	 * Creates a new TicketButtonCommand.
	 */
	public TicketButtonCommand() {
		super("ticket-buttons", "Print the ticket button and embed.");
	}

	@Override
	public @Nonnull List<SubcommandData> getSubCommands() {
		return new ArrayList<>();
	}

	@Override
	public @Nonnull List<OptionData> getOptions() {
		return new ArrayList<>();
	}

	@Override
	public @Nonnull CommandPermission getPermission() {
		return CommandPermission.TICKET_BUTTONS;
	}

	@Override
	public void execute(SlashCommandInteractionEvent interaction) {
		if (!( interaction.getChannel() instanceof TextChannel )) {
			return;
		}

		interaction.deferReply(true).queue(hook -> {
			hook.deleteOriginal().queue();
			TextChannel channel = (TextChannel) interaction.getChannel();

			sendEmbed(
					DiscordBot.getInstance().getButtonManager().getButton("open-ticket").formDiscordButton(),
					channel
			);
		});
	}

	/**
	 * Send the embeds
	 *
	 * @param button  the button
	 * @param channel the channel
	 */
	private void sendEmbed(Button button, TextChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Ticket erstellen");
		builder.setDescription(
				"Du möchtest eine Whitelist-Anfrage stellen, einen Spieler bzw. ein Problem melden oder einen Entbannungsantrag für den Server erstellen, so kannst du hier ein Ticket erstellen.\n\n" +
				"Bitte mache dich **vorher** mit unterschiedlichen Tickettypen vertraut! \nDie Übersicht findest du hier: https://server.castcrafter.de/support\n\n" +
				"Allgemeine Fragen sollten in den dafür vorgesehenen öffentlichen Kanälen gestellt werden.\n\n" +
				"Wir bemühen uns die Tickets schnellstmöglich zu bearbeiten, jedoch arbeitet das gesamte Team freiwillig, und gerade unter der Woche kann die Bearbeitung der Tickets länger dauern."
		);
		builder.setColor(Color.GREEN);

		channel.sendMessageEmbeds(builder.build()).setActionRow(button).queue();
	}

}
