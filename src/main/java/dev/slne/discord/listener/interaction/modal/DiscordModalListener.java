package dev.slne.discord.listener.interaction.modal;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.modal.DiscordModal;
import dev.slne.discord.discord.interaction.modal.DiscordModalManager;
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal;
import dev.slne.discord.message.MessageManager;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

import javax.annotation.Nonnull;

/**
 * The type Discord modal listener.
 */
public class DiscordModalListener extends ListenerAdapter {

	private final DiscordModalManager modalManager;

	/**
	 * Instantiates a new Discord modal listener.
	 */
	public DiscordModalListener() {
		this.modalManager = DiscordBot.getInstance().getModalManager();
	}

	@Override
	public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
		final ModalInteraction interaction = event.getInteraction();
		final String modalId = interaction.getModalId();
		final DiscordModal modal = modalManager.getModal(modalId);

		if (modal != null) {
			modal.execute(event);
			return;
		}

		final DiscordStepChannelCreationModal advancedModal = modalManager.getAdvancedModal(modalId);

		if (advancedModal != null) {
			advancedModal.submitModal(event);
			return;
		}

		event.replyEmbeds(MessageManager.getErrorEmbed(
				"Fehler",
				"Deine Aktion konnte nicht durchgeführt werden oder ist abgelaufen."
		)).setEphemeral(true).queue();
	}

}
