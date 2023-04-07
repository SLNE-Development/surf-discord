package dev.slne.discord.ticket.commands;

import java.util.ArrayList;
import java.util.List;

import dev.slne.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.modals.TicketModal;
import dev.slne.discord.ticket.modals.TicketModal.BugReportTicketModal;
import dev.slne.discord.ticket.modals.TicketModal.DiscordSupportTicketModal;
import dev.slne.discord.ticket.modals.TicketModal.ServerSupportTicketModal;
import dev.slne.discord.ticket.modals.TicketModal.WhitelistTicketModal;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TicketCommand extends DiscordCommand {

    public TicketCommand() {
        super("ticket", "Create a ticket");
    }

    @Override
    public List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();

        OptionData type = new OptionData(OptionType.STRING, "type", "The type of ticket you want to create");

        for (TicketType ticketType : TicketType.values())
            type.addChoice(ticketType.getName(), ticketType.name().toLowerCase());

        options.add(type);

        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        TicketType ticketType = TicketType.getByName(event.getOption("type").getAsString());

        TicketModal ticketModal = null;
        if (ticketType.equals(TicketType.SERVER_SUPPORT))
            ticketModal = new ServerSupportTicketModal();
        else if (ticketType.equals(TicketType.DISCORD_SUPPORT))
            ticketModal = new DiscordSupportTicketModal();
        else if (ticketType.equals(TicketType.BUGREPORT))
            ticketModal = new BugReportTicketModal();
        else if (ticketType.equals(TicketType.WHITELIST))
            ticketModal = new WhitelistTicketModal();

        ticketModal.open(event);
    }

}
