package dev.slne.discord.discord.interaction.command;

import java.util.ArrayList;
import java.util.List;

import dev.slne.discord.ticket.commands.TicketCloseCommand;
import dev.slne.discord.ticket.commands.TicketCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

public class DiscordCommandManager {

    private List<DiscordCommand> commands;

    /**
     * Creates a new DiscordCommandManager.
     */
    public DiscordCommandManager() {
        this.commands = new ArrayList<>();

        this.commands.add(new TicketCommand());
        this.commands.add(new TicketCloseCommand());
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
