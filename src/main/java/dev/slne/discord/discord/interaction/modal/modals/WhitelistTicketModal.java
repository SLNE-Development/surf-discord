package dev.slne.discord.discord.interaction.modal.modals;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.interaction.modal.DiscordModal;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import dev.slne.discord.ticket.tickets.WhitelistApplicationTicket;
import dev.slne.discord.whitelist.WhitelistService;
import feign.FeignException;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;

/**
 * The type Whitelist ticket modal.
 */
public class WhitelistTicketModal extends DiscordModal {

	/**
	 * Constructor for a whitelist ticket modal
	 */
	public WhitelistTicketModal() {
		super(TicketType.WHITELIST.getName() + " Ticket erstellen");
	}

	@Override
	public void fillComponents() {
		TextInput minecraftNameInput = TextInput.create("minecraft-name", "Minecraft-Name", TextInputStyle.SHORT)
												.setRequired(true).setMinLength(3).setMaxLength(16).build();

		TextInput discordTwitchVerified = TextInput.create("discord-twitch-verified",
														   "Twitch-Account verbunden?", TextInputStyle.SHORT
												   )
												   .setPlaceholder("Ja").setValue("Nein").setMinLength(2)
												   .setMaxLength(4).setRequired(true).build();

		components.add(minecraftNameInput);
		components.add(discordTwitchVerified);
	}

	@Override
	public void execute(ModalInteractionEvent event) {
		ModalInteraction modalInteraction = event.getInteraction();

		TicketType ticketType = TicketType.getByName(modalInteraction.getModalId());
		if (ticketType == null) {
			event.reply("Es ist ein Fehler beim Abrufen des Ticket-Typs aufgetreten.").setEphemeral(true).queue();
			return;
		}

		modalInteraction.deferReply(true).queue(hook -> {
			if (!ticketType.equals(TicketType.WHITELIST)) {
				return;
			}

			ModalMapping minecraftNameValue = modalInteraction.getValue("minecraft-name");
			if (minecraftNameValue == null) {
				hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
				DataApi.getDataInstance().logError(getClass(), "minecraftNameValue is null");
				return;
			}
			String minecraftName = minecraftNameValue.getAsString();

			if (minecraftName.isEmpty()) {
				hook.editOriginal("Du musst einen Minecraft-Namen angeben!").queue();
				return;
			}

			ModalMapping discordTwitchVerifiedValue = modalInteraction.getValue("discord-twitch-verified");
			if (discordTwitchVerifiedValue == null) {
				hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
				DataApi.getDataInstance().logError(getClass(), "discordTwitchVerifiedValue is null");
				return;
			}
			String discordTwitchVerifiedString = discordTwitchVerifiedValue.getAsString();

			List<String> validInput = Arrays.asList("ja", "yes", "j", "y");
			boolean discordTwitchVerified = validInput.contains(discordTwitchVerifiedString.toLowerCase());

			if (!discordTwitchVerified) {
				hook.editOriginal(
							"Dein Twitch-Account muss mit deinem Discord-Account verknüpft sein, um auf dem Server spielen zu dürfen.")
					.queue();
				return;
			}

			WhitelistService.INSTANCE.getUuidByMinecraftName(minecraftName)
									 .thenAcceptAsync(proxyUuidByMinecraftName -> {
										 if (proxyUuidByMinecraftName == null) {
											 hook.editOriginal("Du musst einen gültigen Minecraft-Java Namen angeben.").
												 queue();
											 return;
										 }

										 UUID uuid = proxyUuidByMinecraftName.getUuid();

										 WhitelistService.INSTANCE.checkWhitelists(uuid, null, null)
																  .thenAcceptAsync(whitelists -> {
																	  if (!whitelists.isEmpty()) {
																		  hook.editOriginal(
																					  "Du bist bereits auf der Whitelist!")
																			  .queue();
																		  return;
																	  }

																	  Ticket ticket = new WhitelistApplicationTicket(
																			  modalInteraction.getGuild(),
																			  modalInteraction.getUser()
																	  );

																	  ticket.openFromButton()
																			.thenAcceptAsync(result -> {
																				if (result.equals(
																						TicketCreateResult.SUCCESS)) {
																					StringBuilder message =
																							new StringBuilder();
																					message.append("Dein \"");
																					message.append(
																							TicketType.WHITELIST.getName());
																					message.append(
																							"\"-Ticket wurde erfolgreich erstellt! ");

																					TextChannel channel =
																							ticket.getChannel();
																					if (channel != null) {
																						message.append(
																								channel.getAsMention());
																					}

																					hook.editOriginal(
																							message.toString()).queue();

																					if (channel != null) {
																						channel.sendMessage(
																									   "Minecraft-Name: `" +
																									   minecraftName + "`")
																							   .queue();
																					}
																				} else if (result.equals(
																						TicketCreateResult.ALREADY_EXISTS)) {
																					hook.editOriginal(
																								"Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo.")
																						.queue();
																				} else if (result.equals(
																						TicketCreateResult.MISSING_PERMISSIONS)) {
																					hook.editOriginal(
																								"Du hast nicht die benötigten Berechtigungen, um ein Ticket zu erstellen!")
																						.queue();
																				} else {
																					hook.editOriginal(
																								"Es ist ein Fehler aufgetreten (cwdskjfkd437489)!")
																						.queue();
																					DataApi.getDataInstance()
																						   .logError(
																								   getClass(),
																								   String.format(
																										   "Error while creating ticket: %s",
																										   result
																								   )
																						   );
																				}
																			}).exceptionally(failure -> {
																				hook.editOriginal(
																							"Es ist ein Fehler aufgetreten (ie823423894kjd)!")
																					.queue();
																				DataApi.getDataInstance()
																					   .logError(
																							   getClass(),
																							   "Error while creating ticket",
																							   failure
																					   );

																				return null;
																			});
																  }).exceptionally(exception -> {
															 hook.editOriginal("Es ist ein Fehler aufgetreten (23232dwdwdwd)!").queue();
															 DataApi.getDataInstance()
																	.logError(getClass(), "Error while creating ticket", exception);

															 return null;
														 });
									 }).exceptionally(failure -> {
								if (failure instanceof CompletionException && failure.getCause() instanceof FeignException.NotFound) {
									hook.editOriginal("Du musst einen gültigen Minecraft-Java Namen angeben.").
										queue();
									return null;
								}

								hook.editOriginal("Es ist ein Fehler aufgetreten (efjkhwefwiekefkp)!").queue();
								DataApi.getDataInstance().logError(getClass(), "Error while creating ticket", failure);

								return null;
							});
		});
	}

	@Override
	public @Nonnull String getCustomId() {
		return TicketType.WHITELIST.name();
	}

}
