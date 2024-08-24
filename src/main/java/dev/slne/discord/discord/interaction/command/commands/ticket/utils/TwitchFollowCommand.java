package dev.slne.discord.discord.interaction.command.commands.ticket.utils;

import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import dev.slne.discord.spring.service.ticket.TicketService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The type Twitch follow command.
 */
@DiscordCommandMeta(
		name = "twitch-follow",
		description = "Fordere einen Benutzer auf CastCrafter auf Twitch zu folgen.",
		permission = CommandPermission.TWITCH_FOLLOW
)
public class TwitchFollowCommand extends TicketCommand {

	@Autowired
	public TwitchFollowCommand(TicketService ticketService) {
		super(ticketService);
	}

	private static final String OPTION_USER = "user";
	private static final String CASTCRAFTER_TWITCH_URL = "https://www.twitch.tv/castcrafter";

	/**
	 * Returns the options of the command.
	 *
	 * @return The options of the command.
	 */
	@NotNull
	@Override
	public List<OptionData> getOptions() {
		return List.of(
				new OptionData(
						OptionType.USER,
						OPTION_USER,
						"Der Benutzer, der aufgefordert wird, CastCrafter auf Twitch zu folgen.",
						true,
						false
				)
		);
	}

	@Override
	public void internalExecute(@NotNull SlashCommandInteractionEvent interaction, InteractionHook hook)
			throws CommandException {
		final OptionMapping userMapping = interaction.getOption("user");

		if (userMapping == null) {
			throw new CommandException("Du musst einen Nutzer angeben.");
		}

		final User user = userMapping.getAsUser();
		getChannel().sendMessage(user.getAsMention())
				.setEmbeds(getEmbed())
				.queue();

		hook.deleteOriginal().queue();
	}

	/**
	 * Returns the embed for the command.
	 *
	 * @return The embed.
	 */
	private @NotNull MessageEmbed getEmbed() {
		return new EmbedBuilder()
				.setTitle("CastCrafter auf Twitch folgen")
				.setDescription("Du folgst CastCrafter nicht auf Twitch." +
								" Bitte folge [CastCrafter auf Twitch](%s), um auf dem Server zu spielen."
								.formatted(CASTCRAFTER_TWITCH_URL))
				.setColor(TwitchConnectCommand.TWITCH_EMBED_COLOR)
				.setTimestamp(Instant.now())
				.build();
	}
}
