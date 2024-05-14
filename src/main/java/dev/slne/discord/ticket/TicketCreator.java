package dev.slne.discord.ticket;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.ticket.result.TicketCreateResult;
import net.dv8tion.jda.api.entities.channel.concrete.Category;

import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket creator.
 */
public class TicketCreator {

	/**
	 * Create ticket.
	 *
	 * @param future          the future
	 * @param ticket          the ticket
	 * @param ticketName      the ticket name
	 * @param channelCategory the channel category
	 * @param runnable        the runnable
	 */
	public static void createTicket(
			CompletableFuture<TicketCreateResult> future, Ticket ticket, String ticketName,
			Category channelCategory, Runnable runnable
	) {
		TicketService.INSTANCE.createTicket(ticket).thenAcceptAsync(ticketCreateResult -> {
			if (ticketCreateResult == null) {
				future.complete(TicketCreateResult.ERROR);
				return;
			}

			DiscordBot.getInstance().getTicketManager()
					  .addTicket(ticket);

			createTicketChannel(future, ticket, ticketName, channelCategory, runnable);
		}).exceptionally(exception -> {
			future.completeExceptionally(exception);
			return null;
		});
	}

	/**
	 * Create ticket channel.
	 *
	 * @param future          the future
	 * @param ticket          the ticket
	 * @param ticketName      the ticket name
	 * @param channelCategory the channel category
	 * @param runnable        the runnable
	 */
	private static void createTicketChannel(
			CompletableFuture<TicketCreateResult> future, Ticket ticket, String ticketName,
			Category channelCategory, Runnable runnable
	) {
		TicketChannel.createTicketChannel(ticket, ticketName, channelCategory)
					 .thenAcceptAsync(ticketChannelCreateResult -> {
						 if (ticketChannelCreateResult == null) {
							 future.complete(TicketCreateResult.ERROR);
							 return;
						 }

						 if (ticketChannelCreateResult != TicketCreateResult.SUCCESS) {
							 future.complete(ticketChannelCreateResult);
							 return;
						 }

						 runAfterOpen(future, ticket, runnable);
					 })
					 .exceptionally(exception -> {
						 exception.printStackTrace();
						 future.completeExceptionally(
								 exception);
						 return null;
					 });
	}

	/**
	 * Run after open.
	 *
	 * @param future   the future
	 * @param ticket   the ticket
	 * @param runnable the runnable
	 */
	private static void runAfterOpen(CompletableFuture<TicketCreateResult> future, Ticket ticket, Runnable runnable) {
		ticket.afterOpen().thenAcceptAsync(v -> {
			runnable.run();
			future.complete(TicketCreateResult.SUCCESS);
		}).exceptionally(exception -> {
			future.completeExceptionally(exception);
			return null;
		});
	}
}
