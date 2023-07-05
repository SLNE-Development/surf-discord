package dev.slne.discord.discord.interaction.command.commands.ticket.members;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.ticket.TicketChannel;
import dev.slne.discord.ticket.member.TicketMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TicketMemberAddCommand extends TicketCommand {

    /**
     * Creates a new TicketMemberAddCommand.
     */
    public TicketMemberAddCommand() {
        super("add", "Füge einen Nutzer zu einem Ticket hinzu.");
    }

    @Override
    public @Nonnull List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();

        options.add(new OptionData(OptionType.USER, "user", "Der Nutzer, der hinzugefügt werden soll.", true, false));

        return options;
    }

    @Override
    public @Nonnull DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_TICKET_ADD_USER;
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        interaction.deferReply(true).queue(hook -> {
            OptionMapping userOption = interaction.getOption("user");

            if (userOption == null) {
                hook.editOriginal("Du musst einen Nutzer angeben.").queue();
                return;
            }

            User user = userOption.getAsUser();

            if (user.equals(DiscordBot.getInstance().getJda().getSelfUser())) {
                hook.editOriginal("Du kannst den Bot nicht hinzufügen.").queue();
                return;
            }

            TicketMember ticketMember = getTicket().getActiveTicketMember(user);

            if (ticketMember != null) {
                hook.editOriginal("Dieser Nutzer ist bereits in diesem Ticket.").queue();
                return;
            }

            TicketMember newTicketMember = new TicketMember(getTicket(), user, interaction.getUser());
            getTicket().addTicketMember(newTicketMember).whenComplete(createdTicketMember -> {
                if (createdTicketMember == null) {
                    hook.editOriginal("Der Nutzer konnte nicht hinzugefügt werden.").queue();
                    return;
                }

                TicketChannel.addTicketMember(getTicket(), newTicketMember).whenComplete(v -> {
                    hook.editOriginal("Der Nutzer wurde erfolgreich hinzugefügt.").queue();
                    getChannel().sendMessage(user.getAsMention()).setEmbeds(getAddedEmbed(interaction.getUser()))
                            .queue();
                }, failure -> Launcher.getLogger()
                        .logError("Error while updating channel permissions: " + failure.getMessage()));
            });
        });
    }

    /**
     * Returns the embed that is sent when a user is added to a ticket.
     *
     * @return The embed that is sent when a user is added to a ticket.
     */
    public MessageEmbed getAddedEmbed(User adder) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Willkommen im Ticket!");
        builder.setDescription(
                "Du wurdest zu einem Ticket hinzugefügt. Bitte sieh dir den Verlauf des Tickets an und warte auf eine Nachricht eines Teammitglieds.");
        builder.setTimestamp(Instant.now());
        builder.setColor(Color.WHITE);
        builder.setFooter("Hinzugefügt von " + adder.getName(), adder.getAvatarUrl());

        return builder.build();
    }

}
