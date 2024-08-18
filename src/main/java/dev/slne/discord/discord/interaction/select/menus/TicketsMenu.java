package dev.slne.discord.discord.interaction.select.menus;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.interaction.modal.modals.UnbanTicketModal;
import dev.slne.discord.discord.interaction.modal.modals.WhitelistTicketModal;
import dev.slne.discord.discord.interaction.modal.step.creator.report.ReportTicketChannelCreationModal;
import dev.slne.discord.discord.interaction.select.DiscordSelectMenu;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import dev.slne.discord.ticket.tickets.BugreportTicket;
import dev.slne.discord.ticket.tickets.DiscordSupportTicket;
import dev.slne.discord.ticket.tickets.EventSupportTicket;
import dev.slne.discord.ticket.tickets.ServerSupportTicket;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket menu.
 */
public class TicketsMenu extends DiscordSelectMenu {

	/**
	 * Instantiates a new Ticket menu.
	 *
	 * @param idSuffix the id suffix
	 */
	public TicketsMenu(String idSuffix) {
		super("menu:tickets-" + idSuffix, "Ticket auswählen", Arrays.stream(TicketType.values())
																	.map(ticketType -> DiscordSelectMenu.DiscordSelectMenuOption.of(
																			ticketType.getName(),
																			ticketType.getConfigName(),
																			ticketType.getDescription().substring(
																					0,
																					Math.min(
																							ticketType.getDescription()
																									  .length(),
																							100
																					)
																			),
																			ticketType.getEmoji()
																	)).toList(), 1, 1);
	}

	@Override
	public void onSelect(StringSelectInteraction interaction, List<DiscordSelectMenuOption> selectedOptions) {
		DiscordSelectMenuOption option = selectedOptions.get(0);
		TicketType ticketType = TicketType.getByName(option.getLabel());

		if (ticketType.equals(TicketType.WHITELIST)) {
			handleWhitelist(ticketType, interaction);
			return;
		}

		if (ticketType.equals(TicketType.UNBAN)) {
			handleUnban(ticketType, interaction);
			return;
		}

		if (ticketType.equals(TicketType.REPORT)) { // TODO: 18.08.2024 10:32 - special treatment for ChannelCreationModals
			handleReport(ticketType, interaction);
			return;
		}

		interaction.deferReply(true).queue(hook -> {
			User user = interaction.getUser();
			Guild guild = interaction.getGuild();

			CompletableFuture<Ticket> ticketFuture = new CompletableFuture<>();

			if (ticketType.equals(TicketType.DISCORD_SUPPORT)) {
				ticketFuture.complete(new DiscordSupportTicket(guild, user));
			} else {
				Whitelist.isWhitelisted(user).thenAcceptAsync(whitelisted -> {
					List<TicketType> whitelistedTypes = List.of(
							TicketType.SURVIVAL_SUPPORT
					);

					if (!whitelisted && whitelistedTypes.contains(ticketType)) {
						sendNotWhitelistedMessage(hook);
						return;
					}

					Ticket ticket = null;
					switch (ticketType) {
						case EVENT_SUPPORT -> ticket = new EventSupportTicket(guild, user);
						case SURVIVAL_SUPPORT -> ticket = new ServerSupportTicket(guild, user);
						case BUGREPORT -> ticket = new BugreportTicket(guild, user);
						default -> {
						}
					}

					ticketFuture.complete(ticket);
				});
			}

			ticketFuture.thenAcceptAsync(ticket -> {
				if (ticket == null) {
					hook.editOriginal("Es konnte kein Ticket mit dem angegebenen Ticket-Typen erstellt werden!")
						.queue();
					return;
				}

				ticket.openFromButton().thenAcceptAsync(result -> {
					if (result.equals(TicketCreateResult.SUCCESS)) {
						StringBuilder message = new StringBuilder();
						message.append("Dein \"");
						message.append(ticketType.getName());
						message.append("\"-Ticket wurde erfolgreich erstellt! ");

						if (ticket.getChannel() != null) {
							message.append(ticket.getChannel().getAsMention());
						}

						hook.editOriginal(message.toString()).queue();
					} else if (result.equals(TicketCreateResult.ALREADY_EXISTS)) {
						hook.editOriginal(
									"Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo.")
							.queue();
					} else if (result.equals(TicketCreateResult.MISSING_PERMISSIONS)) {
						hook.editOriginal("Du hast nicht die benötigten Berechtigungen, um ein Ticket zu erstellen!")
							.queue();
					} else {
						hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
						DataApi.getDataInstance()
							   .logError(getClass(), String.format("Error while creating ticket: %s", result));
					}
				}).exceptionally(failure -> {
					hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
					DataApi.getDataInstance().logError(getClass(), "Error while creating ticket", failure);

					return null;
				});
			}).exceptionally(failure -> {
				hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
				DataApi.getDataInstance().logError(getClass(), "Error while creating ticket", failure);

				return null;
			});
			interaction.editSelectMenu(interaction.getSelectMenu()).queue();
		});

	}


	/**
	 * Handles the whitelist button
	 *
	 * @param ticketType  the ticket type
	 * @param interaction the interaction
	 */
	private void handleWhitelist(TicketType ticketType, StringSelectInteraction interaction) {
		if (!ticketType.equals(TicketType.WHITELIST)) {
			return;
		}

		User user = interaction.getUser();

		Whitelist.isWhitelisted(user).thenAcceptAsync(whitelistedBoolean -> {
			boolean whitelisted = whitelistedBoolean;

			if (whitelisted) {
				sendAlreadyWhitelistedMessage(interaction);
				return;
			}

			WhitelistTicketModal whitelistModal = new WhitelistTicketModal();
			Modal modal = whitelistModal.buildModal();
			interaction.replyModal(modal).queue();
			interaction.editSelectMenu(interaction.getSelectMenu()).queue();
		}).exceptionally(failure -> {
			interaction.reply("Es ist ein Fehler aufgetreten!").setEphemeral(true).queue();
			interaction.editSelectMenu(interaction.getSelectMenu()).queue();
			DataApi.getDataInstance().logError(getClass(), "Error while checking if user is whitelisted", failure);

			return null;
		});
	}

	/**
	 * Handles the unban button
	 *
	 * @param ticketType  the ticket type
	 * @param interaction the interaction
	 */
	private void handleUnban(TicketType ticketType, StringSelectInteraction interaction) {
		if (!ticketType.equals(TicketType.UNBAN)) {
			return;
		}

		UnbanTicketModal unbanTicketModal = new UnbanTicketModal();
		Modal modal = unbanTicketModal.buildModal();
		interaction.replyModal(modal).queue();
		interaction.editSelectMenu(interaction.getSelectMenu()).queue();

	}

	private void handleReport(TicketType ticketType, StringSelectInteraction interaction) {
		final ReportTicketChannelCreationModal reportTicketModal = new ReportTicketChannelCreationModal();

		reportTicketModal.startChannelCreation(interaction)
				.thenRunAsync(() -> interaction.editSelectMenu(interaction.getSelectMenu()).queue())
				.exceptionally(failure -> {
					DataApi.getDataInstance().logError(getClass(), "Error while creating report ticket", failure);
					return null;
				});
	}

	/**
	 * Sends a message to the user that he is not whitelisted
	 *
	 * @param hook the hook
	 */
	private void sendNotWhitelistedMessage(InteractionHook hook) {
		hook.editOriginal("Du befindest dich nicht auf der Whitelist und kannst dieses Ticket nicht öffnen.").queue();
	}

	/**
	 * Sends a message to the user that he is allready whitelisted
	 *
	 * @param interaction the interaction
	 */
	private void sendAlreadyWhitelistedMessage(StringSelectInteraction interaction) {
		interaction.reply("Du befindest dich bereits auf der Whitelist und kannst dieses Ticket nicht öffnen.")
				   .setEphemeral(true).queue();
	}
}
