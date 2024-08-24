package dev.slne.discord.discord.interaction.command;

/**
 * The type Discord command manager.
 */
public class DiscordCommandManager {

//	private final List<DiscordCommand> commands;
//
//	/**
//	 * Creates a new DiscordCommandManager.
//	 */
//	public DiscordCommandManager() {
//		this.commands = new ArrayList<>();

//		this.commands.add(new TicketButtonCommand());
//		this.commands.add(new TicketCloseCommand());
//		this.commands.add(new TicketMemberAddCommand());
//		this.commands.add(new TicketMemberRemoveCommand());

//		this.commands.add(new TwitchConnectCommand());
//		this.commands.add(new TwitchFollowCommand());
//		this.commands.add(new TicketDependenciesNotMetCommand());

//		this.commands.add(new WhitelistCommand());
//		this.commands.add(new WhitelistedCommand());
//		this.commands.add(new WhitelistQueryCommand());
//		this.commands.add(new WhitelistRoleRemoveCommand());

//		this.commands.add(new ReactionRoleTextCommand());
//	}

//	/**
//	 * Finds a command
//	 *
//	 * @param name The name of the command.
//	 *
//	 * @return The command.
//	 */
//	public DiscordCommand findCommand(String name) {
//		return this.commands.stream().filter(command -> command.getName().equals(name)).findFirst().orElse(null);
//	}

//	/**
//	 * Registers the commands to a guild.
//	 *
//	 * @param guild The guild.
//	 */
//	public void registerToGuild(@NotNull Guild guild) {
//		CommandListUpdateAction updateAction = guild.updateCommands();
//		List<CommandData> commandDatas = new ArrayList<>();
//
//		this.commands.forEach(command -> {
//			SlashCommandData slashCommandData = Commands.slash(command.getName(), command.getDescription());
//			slashCommandData.setGuildOnly(command.isGuildOnly());
//			slashCommandData.setNSFW(command.isNsfw());
//			slashCommandData.addSubcommands(command.getSubCommands());
//			slashCommandData.addOptions(command.getOptions());
//			commandDatas.add(slashCommandData);
//		});
//
////        updateAction.addCommands(commandDatas).queue(
////                cmds -> {
////                    String cmdNames = cmds.stream().map(Command::getName).reduce((a, b) -> a + ", " + b).orElse("");
////                    DataApi.getDataInstance().logInfo(getClass(), String.format("Registered commands [%s] to guild %s.",
////                            cmdNames, guild.getName()));
////                },
////                throwable -> {
////                    DataApi.getDataInstance().logError(getClass(), String.format("Failed to register commands to " +
////                            "guild %s", guild.getName()), throwable);
////                });
//
//		DataApi.getDataInstance()
//			   .logInfo(
//					   getClass(),
//					   String.format("Registered commands [%s] to guild %s.", commandDatas, guild.getName())
//			   );
//	}
}
