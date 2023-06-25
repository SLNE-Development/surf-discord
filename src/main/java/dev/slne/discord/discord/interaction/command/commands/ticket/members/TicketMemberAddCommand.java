package dev.slne.discord.discord.interaction.command.commands.ticket.members;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.Times;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketRepository;
import dev.slne.discord.ticket.member.TicketMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TicketMemberAddCommand extends DiscordCommand {

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
    public void execute(SlashCommandInteractionEvent interaction) {
        if (!(interaction.getChannel() instanceof TextChannel)) {
            interaction.reply("Dieser Befehl kann nur in einem Ticket verwendet werden.").queue();
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

            Ticket ticket = ticketOptional.get();
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

            Optional<TicketMember> ticketMemberOptional = ticket.getActiveTicketMember(user);

            if (ticketMemberOptional.isPresent()) {
                hook.editOriginal("Dieser Nutzer ist bereits in diesem Ticket.").queue();
                return;
            }

            TicketMember ticketMember = new TicketMember(ticket, user, interaction.getUser());
            ticket.addTicketMember(ticketMember).whenComplete(ticketMemberCreateOptional -> {
                if (!ticketMemberCreateOptional.isPresent()) {
                    hook.editOriginal("Der Nutzer konnte nicht hinzugefügt werden.").queue();
                    return;
                }

                hook.editOriginal("Der Nutzer wurde erfolgreich hinzugefügt.").queue();

                channel.sendMessage(user.getAsMention()).setEmbeds(getAddedEmbed(interaction.getUser())).queue();
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
        builder.setTimestamp(Times.now());
        builder.setColor(Color.WHITE);
        builder.setFooter("Hinzugefügt von " + adder.getAsMention(), adder.getAvatarUrl());

        return builder.build();
    }

}
