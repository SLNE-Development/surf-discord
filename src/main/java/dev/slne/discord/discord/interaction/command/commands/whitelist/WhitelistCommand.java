package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.whitelist.Whitelist;
import dev.slne.discord.whitelist.WhitelistService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Whitelist command.
 */
public class WhitelistCommand extends DiscordCommand {

	/**
	 * Creates a new WhitelistCommand.
	 */
	public WhitelistCommand() {
		super("whitelist", "Füge einen Spieler zur Whitelist hinzu.");
	}

	@Override
	public @Nonnull List<SubcommandData> getSubCommands() {
		return new ArrayList<>();
	}

	@Override
	public @Nonnull List<OptionData> getOptions() {
		List<OptionData> options = new ArrayList<>();

		options.add(new OptionData(OptionType.USER, "user", "Der Spieler, der zur Whitelist hinzugefügt werden soll.")
							.setRequired(true));

		options.add(new OptionData(OptionType.STRING, "minecraft", "Der Minecraft Name des Spielers.")
							.setRequired(true));

		options.add(new OptionData(OptionType.STRING, "twitch", "Der Twitch Name des Spielers.")
							.setRequired(true));

		return options;
	}

	@Override
	public @Nonnull DiscordPermission getPermission() {
		return DiscordPermission.USE_COMMAND_WHITELIST;
	}

	@Override
	public void execute(SlashCommandInteractionEvent interaction) {
		if (!( interaction.getChannel() instanceof TextChannel channel )) {
			interaction.reply("Dieser Befehl kann nur in Textkanälen verwendet werden.").setEphemeral(true).queue();
			return;
		}

		InteractionHook hook = interaction.deferReply(true).complete();

		OptionMapping userOption = interaction.getOption("user");
		OptionMapping minecraftOption = interaction.getOption("minecraft");
		OptionMapping twitchOption = interaction.getOption("twitch");

		if (userOption == null) {
			hook.editOriginal("Du musst einen Nutzer angeben.").queue();
			return;
		}

		if (minecraftOption == null) {
			hook.editOriginal("Du musst einen Minecraft Namen angeben.").queue();
			return;
		}

		if (twitchOption == null) {
			hook.editOriginal("Du musst einen Twitch Namen angeben.").queue();
			return;
		}

		User user = userOption.getAsUser();
		String minecraft = minecraftOption.getAsString();
		String twitch = twitchOption.getAsString();
		String discordId = user.getId();

		if (minecraft.length() > 16) {
			hook.editOriginal("Der Minecraft Name darf nicht länger als 16 Zeichen sein.").queue();
			return;
		}

		DataApi.getUuidByPlayerName(minecraft).thenAcceptAsync(uuid -> {
			if (uuid == null) {
				hook.deleteOriginal().queue();
				channel.sendMessage("Der Spieler konnte nicht gefunden werden.").queue();
				return;
			}

			WhitelistService.INSTANCE.checkWhitelists(uuid, discordId, twitch).thenAcceptAsync(whitelists -> {
				if (whitelists != null && !whitelists.isEmpty()) {
					hook.deleteOriginal().queue();
					channel.sendMessage("Der Spieler befindet sich bereits auf der Whitelist.").queue();

					for (Whitelist whitelist : whitelists) {
						Whitelist.getWhitelistQueryEmbed(whitelist).thenAcceptAsync(embed -> {
							if (embed != null) {
								channel.sendMessageEmbeds(embed).queue();
							}
						}).exceptionally(throwable -> {
							DataApi.getDataInstance()
								   .logError(getClass(), "Failed to send whitelist query embed.", throwable);
							return null;
						});
					}

					return;
				}

				WhitelistService.INSTANCE.addWhitelist(new Whitelist(uuid, minecraft, twitch, user,
																	 interaction.getUser()
				)).thenAcceptAsync(whitelist -> {
					if (whitelist == null) {
						hook.deleteOriginal().queue();
						channel.sendMessage("Es ist ein Fehler aufgetreten. Die Whitelist konnte nicht erstellt " +
											"werden.").queue();
						return;
					}

					Guild guild = interaction.getGuild();
					if (guild != null) {
						DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);
						Role whitelistedRole = discordGuild.getWhitelistedRole();

						if (whitelistedRole != null) {
							guild.addRoleToMember(user, whitelistedRole).queue();
						}
					}

					MessageEmbed embed = Whitelist.getWhitelistQueryEmbed(whitelist).join();

					if (embed != null) {
						hook.deleteOriginal().queue();
						channel.sendMessage(user.getAsMention() + " befindet sich nun auf der Whitelist.")
							   .setEmbeds(embed).queue();
					}
				}).exceptionally(throwable -> {
					hook.deleteOriginal().queue();
					channel.sendMessage("Es ist ein Fehler aufgetreten. Die Whitelist konnte nicht erstellt werden.")
						   .queue();

					DataApi.getDataInstance().logError(getClass(), "Failed to create whitelist entry.", throwable);

					return null;
				});
			}).exceptionally(throwable -> {
				hook.deleteOriginal().queue();
				channel.sendMessage("Es ist ein Fehler aufgetreten. Die Whitelist konnte nicht abgerufen werden.")
					   .queue();

				DataApi.getDataInstance().logError(getClass(), "Failed to get whitelist entries.", throwable);

				return null;
			});
		}).exceptionally(throwable -> {
			hook.deleteOriginal().queue();
			channel.sendMessage("Der Spieler konnte nicht gefunden werden.").queue();

			DataApi.getDataInstance().logError(getClass(), "Failed to get uuid by player name.", throwable);

			return null;
		});
	}

}
