package dev.slne.discord.discord.interaction.command.commands.ticket.utils;

import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TwitchFollowCommand extends TicketCommand {

    /**
     * Creates a new TwitchFollowCommand.
     */
    public TwitchFollowCommand() {
        super("twitch-follow", "Fordere einen Benutzer auf CastCrafter auf Twitch zu folgen.");
    }

    /**
     * Returns the subcommands of the command.
     *
     * @return The subcommands of the command.
     */
    @NotNull
    @Override
    public List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    /**
     * Returns the options of the command.
     *
     * @return The options of the command.
     */
    @NotNull
    @Override
    public List<OptionData> getOptions() {
        final List<OptionData> options = new ArrayList<>();

        options.add(new OptionData(
                OptionType.USER,
                "user",
                "Der Nutzer, der CastCrafter nicht auf Twitch folgt.",
                true,
                false
        ));

        return options;
    }

    /**
     * Returns the permission needed to run this command
     *
     * @return the permission
     */
    @NotNull
    @Override
    public DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_TWITCH_FOLLOW;
    }

    /**
     * Executes the command.
     *
     * @param interaction The interaction.
     */
    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        interaction.deferReply(true).queue(hook -> {
            final OptionMapping userMapping = interaction.getOption("user");

            if (userMapping == null) {
                hook.editOriginal("Du musst einen Nutzer angeben.").queue();
                return;
            }

            final User user = userMapping.getAsUser();
            getChannel().sendMessage(user.getAsMention()).setEmbeds(getEmbed()).queue();

            hook.deleteOriginal().queue();
        });
    }

    /**
     * Returns the embed for the command.
     *
     * @return The embed.
     */
    private @NotNull MessageEmbed getEmbed() {
        return new EmbedBuilder()
                .setTitle("CastCrafter auf Twitch folgen")
                .setDescription("Du folgst CastCrafter nicht auf Twitch." +
                        " Bitte folge CastCrafter auf Twitch, um auf dem Server zu spielen.")
                .setColor(0x6441A5)
                .setTimestamp(Instant.now())
                .build();
    }
}
