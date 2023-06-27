package dev.slne.discord.discord.interaction.command.commands.ticket.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import dev.slne.discord.datasource.Times;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TwitchConnectCommand extends DiscordCommand {

    /**
     * Creates a new TwitchConnectCommand.
     */
    public TwitchConnectCommand() {
        super("twitch-connect",
                "Fordere einen Benutzer auf seinen Twitch-Account mit dem Discord-Account zu verbinden.");
    }

    @Override
    public @Nonnull List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();

        options.add(new OptionData(OptionType.USER, "user", "Der Nutzer, der seinen Twitch-Account verbinden soll.",
                true, false));

        return options;
    }

    @Override
    public @Nonnull DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_TWITCH_CONNECT;
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        if (!(interaction.getChannel() instanceof TextChannel)) {
            interaction.reply("Dieser Befehl kann nur in einem Ticket verwendet werden.").setEphemeral(true).queue();
            return;
        }

        interaction.deferReply(true).queue(hook -> {
            TextChannel channel = (TextChannel) interaction.getChannel();
            Optional<Ticket> ticketOptional = TicketRepository.getTicketByChannel(channel.getId());

            if (!ticketOptional.isPresent()) {
                hook.editOriginal("Dieser Befehl kann nur in einem Ticket verwendet werden.")
                        .queue();
                return;
            }

            OptionMapping userOption = interaction.getOption("user");

            if (userOption == null) {
                hook.editOriginal("Du musst einen Nutzer angeben.").queue();
                return;
            }

            User user = userOption.getAsUser();
            channel.sendMessage(user.getAsMention()).setEmbeds(getEmbed()).queue();

            hook.deleteOriginal().queue();
        });
    }

    /**
     * Gets the embed for the command.
     *
     * @return The embed.
     */
    public MessageEmbed getEmbed() {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Twitch-Account verbinden");
        builder.setDescription(
                "Bitte verbinde deinen Twitch-Account mit Discord, um auf dem Server zu spielen. Wie du dies tun kannst, findest du hier: <#983479094983397406>");
        builder.setColor(Color.decode("#6441A5"));
        builder.setTimestamp(Times.now());

        return builder.build();
    }

}
