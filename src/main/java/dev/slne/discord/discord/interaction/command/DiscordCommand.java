package dev.slne.discord.discord.interaction.command;

import java.util.List;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class DiscordCommand {

    private @Nonnull String name;
    private @Nonnull DefaultMemberPermissions defaultMemberPermissions;
    private @Nonnull String description;
    private boolean guildOnly;
    private boolean nsfw;

    private SlashCommandData commandData;

    /**
     * Creates a new DiscordCommand.
     *
     * @param name        The name of the command.
     * @param description The description of the command.
     */
    protected DiscordCommand(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;

        this.guildOnly = true;
        this.nsfw = false;

        this.defaultMemberPermissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);
        this.commandData = Commands.slash(name, description);
    }

    /**
     * Returns the subcommands of the command.
     *
     * @return The subcommands of the command.
     */
    public abstract @Nonnull List<SubcommandData> getSubCommands();

    /**
     * Returns the options of the command.
     *
     * @return The options of the command.
     */
    public abstract @Nonnull List<OptionData> getOptions();

    /**
     * Executes the command.
     *
     * @param interaction The interaction.
     */
    public abstract void execute(SlashCommandInteractionEvent interaction);

    /**
     * Returns the name of the command.
     *
     * @return The name of the command.
     */
    public @Nonnull String getName() {
        return name;
    }

    /**
     * Returns the default member permissions of the command.
     *
     * @return The default member permissions of the command.
     */
    public @Nonnull DefaultMemberPermissions getDefaultMemberPermissions() {
        return defaultMemberPermissions;
    }

    /**
     * Returns the description of the command.
     *
     * @return The description of the command.
     */
    public @Nonnull String getDescription() {
        return description;
    }

    /**
     * Returns whether the command is guild only.
     *
     * @return Whether the command is guild only.
     */
    public boolean isGuildOnly() {
        return guildOnly;
    }

    /**
     * Returns whether the command is NSFW.
     *
     * @return Whether the command is NSFW.
     */
    public boolean isNsfw() {
        return nsfw;
    }

    /**
     * Returns the SlashCommandData of the command.
     *
     * @return The SlashCommandData of the command.
     */
    public SlashCommandData getCommandData() {
        return commandData;
    }

}
