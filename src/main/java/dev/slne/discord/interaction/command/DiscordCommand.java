package dev.slne.discord.interaction.command;

import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class DiscordCommand {

    private String name;
    private DefaultMemberPermissions defaultMemberPermissions;
    private String description;
    private boolean guildOnly;
    private boolean nsfw;

    private SlashCommandData commandData;

    public DiscordCommand(String name, String description) {
        this.name = name;
        this.description = description;

        this.guildOnly = true;
        this.nsfw = false;

        this.defaultMemberPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);

        SlashCommandData commandData = Commands.slash(name, description);
        this.commandData = commandData;
    }

    public abstract List<SubcommandData> getSubCommands();

    public abstract List<OptionData> getOptions();

    public abstract void execute(SlashCommandInteractionEvent interaction);

    public String getName() {
        return name;
    }

    public DefaultMemberPermissions getDefaultMemberPermissions() {
        return defaultMemberPermissions;
    }

    public String getDescription() {
        return description;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public SlashCommandData getCommandData() {
        return commandData;
    }

}
