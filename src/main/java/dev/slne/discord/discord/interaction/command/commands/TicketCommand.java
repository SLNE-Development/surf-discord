package dev.slne.discord.discord.interaction.command.commands;

import javax.annotation.Nonnull;

import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class TicketCommand extends DiscordCommand {

    private Ticket ticket;
    private TextChannel channel;

    /**
     * Creates a new TicketCommand.
     */
    protected TicketCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    @Override
    public void internalExecute(SlashCommandInteractionEvent interaction) {
        User user = interaction.getUser();
        Guild guild = interaction.getGuild();

        if (!performDiscordCommandChecks(user, guild, interaction)) {
            return;
        }

        if (!(interaction.getChannel() instanceof TextChannel)) {
            interaction.reply("Dieser Befehl kann nur in einem Ticket verwendet werden.").setEphemeral(true).queue();
            return;
        }

        TextChannel textChannel = (TextChannel) interaction.getChannel();
        Ticket ticketGet = TicketRepository.getTicketByChannel(textChannel.getId());

        if (ticketGet == null) {
            interaction.reply("Dieser Befehl kann nur in einem Ticket verwendet werden.").setEphemeral(true)
                    .queue();
            return;
        }

        this.ticket = ticketGet;
        this.channel = textChannel;

        execute(interaction);
    }

    /**
     * @return the ticket
     */
    public Ticket getTicket() {
        return ticket;
    }

    /**
     * @return the channel
     */
    public TextChannel getChannel() {
        return channel;
    }

}
