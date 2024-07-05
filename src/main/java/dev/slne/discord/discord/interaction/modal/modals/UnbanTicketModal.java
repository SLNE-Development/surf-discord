package dev.slne.discord.discord.interaction.modal.modals;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.interaction.modal.DiscordModal;
import dev.slne.discord.punishment.PunishmentService;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import dev.slne.discord.ticket.tickets.UnbanTicket;
import feign.FeignException;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletionException;

public class UnbanTicketModal extends DiscordModal {

	/**
	 * Constructor for a unban ticket modal
	 */
	public UnbanTicketModal() { super( TicketType.UNBAN.getName() + "  erstellen"); }

	@Override
	public void fillComponents() {
		TextInput punishmentIdInput =
				TextInput.create("punishment-id", "Deine Punishment ID", TextInputStyle.SHORT).setRequired(true).setMinLength(6).setMaxLength(6).build();
		components.add(punishmentIdInput);
	}

	@Override
	public void execute(ModalInteractionEvent event) {
		ModalInteraction modalInteraction = event.getInteraction();

		modalInteraction.deferReply(true).queue(hook -> {

			ModalMapping punishmentIdValue = modalInteraction.getValue("punishment-id");
			if (punishmentIdValue == null) {
				hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
				return;
			}

			String punishmentId = punishmentIdValue.getAsString();

			if(punishmentId.isEmpty()) {
				hook.editOriginal("Du hast keine gültige ID eingegeben!").queue();
				return;
			}

			PunishmentService.INSTANCE.getBanByPunishmentId(punishmentId).thenAccept(ban -> {
				if(ban == null) {
					hook.editOriginal("Es konnte kein Bann mit der eingegeben ID gefunden werden!").queue();
					return;
				}

				Ticket ticket = new UnbanTicket(
						modalInteraction.getGuild(),
						modalInteraction.getUser()
				);

				ticket.openFromButton().thenAcceptAsync(result -> {
					if (result.equals(TicketCreateResult.SUCCESS)) {

						StringBuilder message = new StringBuilder();
						message.append("Dein \"");
						message.append(TicketType.UNBAN.getName());
						message.append("\"-Ticket wurde erfolgreich erstellt! ");

						TextChannel channel = ticket.getChannel();

						if (channel != null) {
							message.append(channel.getAsMention());
						}

						hook.editOriginal(message.toString()).queue();

						if (channel != null) {
							channel.sendMessage("PunishmentID: `" + punishmentId + "`").queue();
						}
					} else if (result.equals(TicketCreateResult.ALREADY_EXISTS)) {
						hook.editOriginal("Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo.").queue();

					} else if (result.equals(TicketCreateResult.MISSING_PERMISSIONS)) {
						hook.editOriginal("Du hast nicht die benötigten Berechtigungen, um ein Ticket zu erstellen!").queue();

					} else {
						hook.editOriginal("Es ist ein Fehler aufgetreten (cwdskjfkd437489)!").queue();
						DataApi.getDataInstance().logError(getClass(), String.format("Error while creating ticket: %s", result));
					}

				}).exceptionally(failure -> {
					hook.editOriginal("Es ist ein Fehler aufgetreten (ie823423894kjd)!").queue();
					DataApi.getDataInstance().logError(getClass(), "Error while creating ticket", failure);
					return null;

				});

			}).exceptionally(failure -> {
				if (failure instanceof CompletionException && failure.getCause() instanceof FeignException.NotFound) {
					hook.editOriginal("Es konnte kein Bann mit der eingegeben ID gefunden werden!").queue();
					return null;
				}

				hook.editOriginal("Es ist ein Fehler aufgetreten (efjkhwefwiekefkp)!").queue();
				DataApi.getDataInstance().logError(getClass(), "Error while creating ticket", failure);

				return null;
			});
		});

	}

	@NotNull
	@Override
	public String getCustomId() {
		return TicketType.UNBAN.name();
	}

}
