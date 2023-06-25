package dev.slne.discord.discord.interaction.command.commands.ticket.members;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketRepository;
import dev.slne.discord.ticket.member.TicketMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TicketMemberRemoveCommand extends DiscordCommand {

    /**
     * Creates a new TicketMemberRemoveCommand.
     */
    public TicketMemberRemoveCommand() {
        super("remove", "Entferne einen Nutzer von einem Ticket.");
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

            Ticket ticket = ticketOptional.get();
            OptionMapping userOption = interaction.getOption("user");

            if (userOption == null) {
                hook.editOriginal("Du musst einen Nutzer angeben.").queue();
                return;
            }

            User user = userOption.getAsUser();

            if (user.equals(DiscordBot.getInstance().getJda().getSelfUser())) {
                hook.editOriginal("Du kannst den Bot nicht entfernen.").queue();
                return;
            }

            Optional<TicketMember> ticketMemberOptional = ticket.getActiveTicketMember(user);

            if (ticketMemberOptional.isEmpty()) {
                hook.editOriginal("Dieser Nutzer ist nicht in diesem Ticket.").queue();
                return;
            }

            TicketMember ticketMember = ticketMemberOptional.get();

            if (ticketMember.isRemoved()) {
                hook.editOriginal("Dieser Nutzer wurde bereits entfernt.").queue();
                return;
            }

            ticketMember.setRemovedBy(Optional.of(interaction.getUser()));
            ticket.removeTicketMember(ticketMember).whenComplete(ticketMemberRemovedOptional -> {
                if (ticketMemberRemovedOptional.isEmpty()) {
                    hook.editOriginal("Der Nutzer konnte nicht entfernt werden.").queue();
                    return;
                }

                hook.editOriginal("Der Nutzer wurde entfernt.").queue();

                channel.sendMessage(user.getAsMention() + " wurde von " + interaction.getUser().getAsMention()
                        + " entfernt.").queue();
            });
        });
    }

}
