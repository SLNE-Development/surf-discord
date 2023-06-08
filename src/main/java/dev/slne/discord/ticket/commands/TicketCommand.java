package dev.slne.discord.ticket.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.modals.TicketModal;
import dev.slne.discord.ticket.modals.TicketModal.BugReportTicketModal;
import dev.slne.discord.ticket.modals.TicketModal.DiscordSupportTicketModal;
import dev.slne.discord.ticket.modals.TicketModal.ServerSupportTicketModal;
import dev.slne.discord.ticket.modals.TicketModal.WhitelistTicketModal;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TicketCommand extends DiscordCommand {

    /**
     * Creates a new {@link TicketCommand}.
     */
    public TicketCommand() {
        super("ticket", "Create a ticket");
    }

    @Override
    public @Nonnull List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();

        OptionData type = new OptionData(OptionType.STRING, "type", "The type of ticket you want to create");

        for (TicketType ticketType : TicketType.values()) {
            type.addChoice(ticketType.getName(), ticketType.name().toLowerCase() + "");
        }

        options.add(type);

        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping typeOption = event.getOption("type");

        if (typeOption == null) {
            event.reply("You need to specify a ticket type.").setEphemeral(true).queue();
            return;
        }

        TicketType ticketType = TicketType.getByName(typeOption.getAsString());

        TicketModal ticketModal = null;
        if (ticketType.equals(TicketType.SERVER_SUPPORT)) {
            ticketModal = new ServerSupportTicketModal();
        } else if (ticketType.equals(TicketType.DISCORD_SUPPORT)) {
            ticketModal = new DiscordSupportTicketModal();
        } else if (ticketType.equals(TicketType.BUGREPORT)) {
            ticketModal = new BugReportTicketModal();
        } else if (ticketType.equals(TicketType.WHITELIST)) {
            ticketModal = new WhitelistTicketModal();
        }

        if (ticketModal == null) {
            event.reply("This ticket type is not supported.").setEphemeral(true).queue();
            return;
        }

        ticketModal.open(event);
    }

}
