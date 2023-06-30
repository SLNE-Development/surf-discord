package dev.slne.discord.discord.interaction.command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.discord.interaction.command.commands.reactionrole.ReactionRoleTextCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.TicketButtonCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.TicketCloseCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.members.TicketMemberAddCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.members.TicketMemberRemoveCommand;
import dev.slne.discord.discord.interaction.command.commands.ticket.utils.TwitchConnectCommand;
import dev.slne.discord.discord.interaction.command.commands.whitelist.WhitelistCommand;
import dev.slne.discord.discord.interaction.command.commands.whitelist.WhitelistQueryCommand;
import dev.slne.discord.discord.interaction.command.commands.whitelist.WhitelistRoleRemoveCommand;
import dev.slne.discord.discord.interaction.command.commands.whitelist.WhitelistedCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class DiscordCommandManager {

    private List<DiscordCommand> commands;

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
    public void registerToGuild(Guild guild) {
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

        updateAction.addCommands(commandDatas).queue(
                cmds -> Launcher.getLogger().logInfo("Registered command " + cmds + " to guild " + guild.getName()));
    }

    /**
     * Clears the commands from a guild.
     *
     * @param guild The guild.
     */
    public SurfFutureResult<Void> clearGuild(Guild guild) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DiscordFutureResult<Void> result = new DiscordFutureResult<>(future);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        CompletableFuture<List<Command>> guildFuture = guild.retrieveCommands().submit();
        CompletableFuture<List<Command>> botFuture = DiscordBot.getInstance().getJda().retrieveCommands().submit();

        CompletableFuture.allOf(guildFuture, botFuture).thenAccept(v -> {
            List<Command> guildCommands = guildFuture.join();
            List<Command> botCommands = botFuture.join();

            List<Command> allCommands = new ArrayList<>();
            allCommands.addAll(guildCommands);
            allCommands.addAll(botCommands);

            allCommands.forEach(command -> {
                String applicationId = command.getApplicationId();
                String botApplicationId = DiscordBot.getInstance().getJda().getSelfUser().getApplicationId();

                if (!applicationId.equals(botApplicationId)) {
                    return;
                }

                CompletableFuture<Void> deleteFuture = command.delete().submit();
                futures.add(deleteFuture);
            });

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                    .thenAccept(v2 -> future.complete(null));
        });

        return result;
    }

}
