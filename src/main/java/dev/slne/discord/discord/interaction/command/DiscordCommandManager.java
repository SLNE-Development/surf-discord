package dev.slne.discord.discord.interaction.command;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.interaction.command.commands.reactionrole.ReactionRoleTextCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.TicketButtonCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.TicketCloseCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.TicketDependenciesNotMetCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.members.TicketMemberAddCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.members.TicketMemberRemoveCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.utils.TwitchConnectCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.utils.TwitchFollowCommand;
import dev.slne.discord.discord.interaction.command.commands.whitelist.WhitelistCommand;
import dev.slne.discord.discord.interaction.command.commands.whitelist.WhitelistQueryCommand;
import dev.slne.discord.discord.interaction.command.commands.whitelist.WhitelistRoleRemoveCommand;
import dev.slne.discord.discord.interaction.command.commands.whitelist.WhitelistedCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Discord command manager.
 */
public class DiscordCommandManager {

	private final List<DiscordCommand> commands;

	/**
	 * Creates a new DiscordCommandManager.
	 */
	public DiscordCommandManager() {
		this.commands = new ArrayList<>();

		this.commands.add(new TicketButtonCommand());
		this.commands.add(new TicketCloseCommand());
		this.commands.add(new TicketMemberAddCommand());
		this.commands.add(new TicketMemberRemoveCommand());

		this.commands.add(new TwitchConnectCommand());
		this.commands.add(new TwitchFollowCommand());
		this.commands.add(new TicketDependenciesNotMetCommand());

		this.commands.add(new WhitelistCommand());
		this.commands.add(new WhitelistedCommand());
		this.commands.add(new WhitelistQueryCommand());
		this.commands.add(new WhitelistRoleRemoveCommand());

		this.commands.add(new ReactionRoleTextCommand());
	}

	/**
	 * Finds a command
	 *
	 * @param name The name of the command.
	 *
	 * @return The command.
	 */
	public DiscordCommand findCommand(String name) {
		return this.commands.stream().filter(command -> command.getName().equals(name)).findFirst().orElse(null);
	}

	/**
	 * Registers the commands to a guild.
	 *
	 * @param guild The guild.
	 */
	public void registerToGuild(@NotNull Guild guild) {
		CommandListUpdateAction updateAction = guild.updateCommands();
		List<CommandData> commandDatas = new ArrayList<>();

		this.commands.forEach(command -> {
			SlashCommandData slashCommandData = Commands.slash(command.getName(), command.getDescription());
			slashCommandData.setGuildOnly(true);
			slashCommandData.setNSFW(false);
			slashCommandData.addSubcommands(command.getSubCommands());
			slashCommandData.addOptions(command.getOptions());
			commandDatas.add(slashCommandData);
		});

//        updateAction.addCommands(commandDatas).queue(
//                cmds -> {
//                    String cmdNames = cmds.stream().map(Command::getName).reduce((a, b) -> a + ", " + b).orElse("");
//                    DataApi.getDataInstance().logInfo(getClass(), String.format("Registered commands [%s] to guild %s.",
//                            cmdNames, guild.getName()));
//                },
//                throwable -> {
//                    DataApi.getDataInstance().logError(getClass(), String.format("Failed to register commands to " +
//                            "guild %s", guild.getName()), throwable);
//                });

		DataApi.getDataInstance()
			   .logInfo(
					   getClass(),
					   String.format("Registered commands [%s] to guild %s.", commandDatas, guild.getName())
			   );
	}
}
