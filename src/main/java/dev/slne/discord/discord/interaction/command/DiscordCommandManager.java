package dev.slne.discord.discord.interaction.command;

import java.util.ArrayList;
import java.util.List;

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
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

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
        this.commands.forEach(command -> {
            CommandCreateAction commandCreateAction = guild.upsertCommand(command.getName(),
                    command.getDescription());

            commandCreateAction.addOptions(command.getOptions());
            commandCreateAction.addSubcommands(command.getSubCommands());

            commandCreateAction.queue();
        });
    }

}
