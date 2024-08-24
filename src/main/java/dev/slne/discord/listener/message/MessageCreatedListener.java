package dev.slne.discord.listener.message;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.spring.annotation.DiscordListener;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.message.TicketMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The type Message created listener.
 */
@DiscordListener
public class MessageCreatedListener extends AbstractMessageListener<MessageReceivedEvent> {

	@Autowired
	public MessageCreatedListener(TicketService ticketService) {
		super(ticketService);
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		processEvent(event);
	}

	@Override
	protected void handleEvent(MessageReceivedEvent event, Ticket ticket) {
		if (event.getMessage().isWebhookMessage()) {
			return;
		}

		ticket.addTicketMessage(TicketMessage.fromTicketAndMessage(ticket, event.getMessage()));
	}
}
