package dev.slne.discord.discord.interaction.command;

import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.guild.role.DiscordRole;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class DiscordCommand {

    private final @Nonnull String name;
    private final @Nonnull DefaultMemberPermissions defaultMemberPermissions;
    private final @Nonnull String description;
    private final boolean guildOnly;
    private final boolean nsfw;

    private final SlashCommandData commandData;

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
     * Returns the permission needed to run this command
     *
     * @return the permission
     */
    public abstract @Nonnull DiscordPermission getPermission();

    /**
     * Executes the command internally
     *
     * @param interaction the interaction event
     */
    public void internalExecute(SlashCommandInteractionEvent interaction) {
        User user = interaction.getUser();
        Guild guild = interaction.getGuild();

        if (!performDiscordCommandChecks(user, guild, interaction)) {
            return;
        }

        execute(interaction);
    }

    /**
     * Performs the checks for the command.
     *
     * @param user        The user.
     * @param guild       The guild.
     * @param interaction The interaction.
     *
     * @return Whether the checks were successful.
     */
    protected boolean performDiscordCommandChecks(User user, Guild guild, SlashCommandInteractionEvent interaction) {
        if (guild == null) {
            interaction.reply("Es ist ein Fehler aufgetreten (dhwfm4nD)").setEphemeral(true).queue();
            return false;
        }

        DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);
        List<DiscordRole> userRoles = discordGuild.getGuildRoles(user.getId()).join();

        boolean hasPermission = false;

        for (DiscordRole userRole : userRoles) {
            if (userRole.hasRolePermission(getPermission())) {
                hasPermission = true;
                break;
            }
        }

        if (!hasPermission) {
            interaction.reply("Du besitzt keine Berechtigung diesen Befehl zu verwenden.").setEphemeral(true).queue();
            return false;
        }

        return true;
    }

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
