package dev.slne.discord.discord.interaction.command.commands;

import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketService;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nonnull;

/**
 * The type Ticket command.
 */
@Getter
public abstract class TicketCommand extends DiscordCommand {

	private Ticket ticket;
	private TextChannel channel;

	/**
	 * Creates a new TicketCommand.
	 *
	 * @param name        the name
	 * @param description the description
	 */
	protected TicketCommand(@Nonnull String name, @Nonnull String description) {
		super(name, description);
	}

	@Override
	public void internalExecute(SlashCommandInteractionEvent interaction) {
		User user = interaction.getUser();
		Guild guild = interaction.getGuild();

		performDiscordCommandChecks(user, guild, interaction).thenAcceptAsync(success -> {
			if (!success) {
				return;
			}

			if (!( interaction.getChannel() instanceof TextChannel textChannel )) {
				interaction.reply("Dieser Befehl kann nur in einem Ticketkanal verwendet werden.").setEphemeral(true)
						   .queue();
				return;
			}

			Ticket ticketGet = TicketService.INSTANCE.getTicketByChannel(textChannel.getId());

			if (ticketGet == null) {
				interaction.reply("Dieser Befehl kann nur in einem Ticket verwendet werden.").setEphemeral(true)
						   .queue();
				return;
			}

			this.ticket = ticketGet;
			this.channel = textChannel;

			execute(interaction);
		}).exceptionally(throwable -> {
			throwable.printStackTrace();
			return null;
		});
	}

}
