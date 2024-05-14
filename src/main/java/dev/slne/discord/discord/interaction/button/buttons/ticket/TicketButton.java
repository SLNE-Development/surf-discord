package dev.slne.discord.discord.interaction.button.buttons.ticket;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.interaction.button.DiscordButton;
import dev.slne.discord.discord.interaction.modal.modals.WhitelistTicketModal;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import dev.slne.discord.ticket.tickets.BugReportTicket;
import dev.slne.discord.ticket.tickets.DiscordSupportTicket;
import dev.slne.discord.ticket.tickets.ServerSupportTicket;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket button.
 */
public abstract class TicketButton extends DiscordButton {

	public static final String WHITELIST_TICKET_ID = "whitelist-ticket";
	public static final String SERVER_SUPPORT_TICKET_ID = "server-support-ticket";
	public static final String DISCORD_SUPPORT_TICKET_ID = "discord-support-ticket";
	public static final String BUGREPORT_TICKET_ID = "bugreport-ticket";

	private final TicketType ticketType;

	/**
	 * The TicketButton
	 *
	 * @param id         the id of the button
	 * @param label      the label of the button
	 * @param emoji      the emoji of the button
	 * @param style      the style
	 * @param ticketType the ticket type of the button
	 */
	protected TicketButton(
			@Nonnull String id, @Nonnull String label, @Nullable Emoji emoji,
			@Nonnull ButtonStyle style, @Nonnull TicketType ticketType
	) {
		super(id, label, emoji, style);

		this.ticketType = ticketType;
	}

	/**
	 * The action of the button
	 *
	 * @param interaction the interaction
	 */
	@Override
	public void onClick(ButtonInteraction interaction) {
		if (ticketType.equals(TicketType.WHITELIST)) {
			handleWhitelist(interaction);
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
							TicketType.SERVER_SUPPORT,
							TicketType.BUGREPORT
					);

					if (!whitelisted && whitelistedTypes.contains(ticketType)) {
						sendNotWhitelistedMessage(hook);
						return;
					}

					Ticket ticket = null;
					switch (ticketType) {
						case SERVER_SUPPORT -> ticket = new ServerSupportTicket(guild, user);
						case BUGREPORT -> ticket = new BugReportTicket(guild, user);
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
						message.append(this.ticketType.getName());
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
		});

	}

	/**
	 * Handles the whitelist button
	 *
	 * @param interaction the interaction
	 */
	private void handleWhitelist(ButtonInteraction interaction) {
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
		}).exceptionally(failure -> {
			interaction.reply("Es ist ein Fehler aufgetreten!").setEphemeral(true).queue();
			DataApi.getDataInstance().logError(getClass(), "Error while checking if user is whitelisted", failure);

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
	private void sendAlreadyWhitelistedMessage(ButtonInteraction interaction) {
		interaction.reply("Du befindest dich bereits auf der Whitelist und kannst dieses Ticket nicht öffnen.")
				   .setEphemeral(true).queue();
	}

	/**
	 * The type Whitelist ticket button.
	 */
	public static class WhitelistTicketButton extends TicketButton {
		/**
		 * The WhitelistTicketButton
		 */
		public WhitelistTicketButton() {
			super(WHITELIST_TICKET_ID, "Whitelist", Emoji.fromUnicode("U+1F512"), ButtonStyle.PRIMARY,
				  TicketType.WHITELIST
			);
		}
	}

	/**
	 * The type Server support ticket button.
	 */
	public static class ServerSupportTicketButton extends TicketButton {
		/**
		 * The ServerSupportTicketButton
		 */
		public ServerSupportTicketButton() {
			super(SERVER_SUPPORT_TICKET_ID, "Minecraft Server-Support", Emoji.fromUnicode("U+2696 U+FE0F"),
				  ButtonStyle.SUCCESS, TicketType.SERVER_SUPPORT
			);
		}
	}

	/**
	 * The type Discord support ticket button.
	 */
	public static class DiscordSupportTicketButton extends TicketButton {
		/**
		 * The DiscordSupportTicketButton
		 */
		public DiscordSupportTicketButton() {
			super(DISCORD_SUPPORT_TICKET_ID, "Discord Server-Support",
				  Emoji.fromUnicode("U+1F9D1 U+200D U+2696 U+FE0F"),
				  ButtonStyle.SUCCESS, TicketType.DISCORD_SUPPORT
			);
		}
	}

	/**
	 * The type Bugreport ticket button.
	 */
	public static class BugreportTicketButton extends TicketButton {
		/**
		 * The BugReportTicketButton
		 */
		public BugreportTicketButton() {
			super(BUGREPORT_TICKET_ID, "Bugreport", Emoji.fromUnicode("U+1F41E"), ButtonStyle.DANGER,
				  TicketType.BUGREPORT
			);
		}
	}
}
