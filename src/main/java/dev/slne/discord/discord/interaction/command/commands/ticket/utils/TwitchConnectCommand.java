package dev.slne.discord.discord.interaction.command.commands.ticket.utils;

import dev.slne.discord.discord.guild.permission.CommandPermission;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Twitch connect command.
 */
public class TwitchConnectCommand extends TicketCommand {

	/**
	 * Creates a new TwitchConnectCommand.
	 */
	public TwitchConnectCommand() {
		super(
				"twitch-connect",
				"Fordere einen Benutzer auf seinen Twitch-Account mit dem Discord-Account zu verbinden."
		);
	}

	@Override
	public @Nonnull List<SubcommandData> getSubCommands() {
		return new ArrayList<>();
	}

	@Override
	public @Nonnull List<OptionData> getOptions() {
		List<OptionData> options = new ArrayList<>();

		options.add(new OptionData(OptionType.USER, "user", "Der Nutzer, der seinen Twitch-Account verbinden soll.",
								   true, false
		));

		return options;
	}

	@Override
	public @Nonnull CommandPermission getPermission() {
		return CommandPermission.TWITCH_CONNECT;
	}

	@Override
	public void execute(SlashCommandInteractionEvent interaction) {
		interaction.deferReply(true).queue(hook -> {
			OptionMapping userOption = interaction.getOption("user");

			if (userOption == null) {
				hook.editOriginal("Du musst einen Nutzer angeben.").queue();
				return;
			}

			User user = userOption.getAsUser();
			getChannel().sendMessage(user.getAsMention()).setEmbeds(getEmbed()).queue();

			hook.deleteOriginal().queue();
		});
	}

	/**
	 * Gets the embed for the command.
	 *
	 * @return The embed.
	 */
	public MessageEmbed getEmbed() {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("Twitch-Account verbinden");
		builder.setDescription(
				"Bitte verbinde deinen Twitch-Account mit Discord, um auf dem Server zu spielen. Wie du dies tun kannst, findest du hier: <#1124438644523012234>");
		builder.setColor(Color.decode("#6441A5"));
		builder.setTimestamp(Instant.now());

		return builder.build();
	}

}
