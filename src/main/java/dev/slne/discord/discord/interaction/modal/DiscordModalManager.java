package dev.slne.discord.discord.interaction.modal;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.interaction.modal.modals.UnbanTicketModal;
import dev.slne.discord.discord.interaction.modal.modals.WhitelistTicketModal;

import dev.slne.discord.discord.interaction.modal.step.creator.report.ReportTicketChannelCreationModal;
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The type Discord modal manager.
 */
public class DiscordModalManager {

	private final Map<String, Class<? extends DiscordModal>> modals;
	private final Object2ObjectMap<String, Supplier<DiscordStepChannelCreationModal>> advancedModals;

	/**
	 * Create a new modal manager
	 */
	public DiscordModalManager() {
		this.modals = new HashMap<>();
		this.advancedModals = new Object2ObjectOpenHashMap<>();

		registerModal(WhitelistTicketModal.class);
		registerModal(UnbanTicketModal.class);
		registerAdvancedModal(ReportTicketChannelCreationModal::new);
	}

	/**
	 * Register a modal
	 *
	 * @param modalClass The class of the modal
	 */
	public void registerModal(Class<? extends DiscordModal> modalClass) {
		if (this.modals.containsValue(modalClass)) {
			return;
		}

		DiscordModal discordModal = getModalByClass(modalClass);

		if (discordModal == null) {
			return;
		}

		String customId = discordModal.getCustomId();
		this.modals.put(customId, modalClass);
	}

	public void registerAdvancedModal(Supplier<DiscordStepChannelCreationModal> creator) {
		final DiscordStepChannelCreationModal modal = creator.get();
		final String modalId = modal.getId();

		advancedModals.putIfAbsent(modalId, creator);
	}

	/**
	 * Get the modal by the class
	 *
	 * @param clazz The class of the modal
	 *
	 * @return DiscordModal modal by class
	 *
	 * @throws IllegalArgumentException If the class doesn't have a constructor
	 */
	private DiscordModal getModalByClass(Class<? extends DiscordModal> clazz) throws IllegalArgumentException {
		DiscordModal discordModal = null;

		if (clazz == null) {
			return null;
		}

		try {
			discordModal = (DiscordModal) clazz.getDeclaredConstructors()[ 0 ].newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				 | SecurityException exception) {
			DataApi.getDataInstance().logError(getClass(), "Failed to create a new instance of the modal class.",
											   exception
			);
		}

		if (discordModal == null) {
			throw new IllegalArgumentException("The modal class must have a constructor without parameters");
		}

		return discordModal;
	}

	/**
	 * Get the modal by the custom id
	 *
	 * @param customId The custom id of the modal
	 *
	 * @return DiscordModal modal
	 */
	public DiscordModal getModal(String customId) {
		return getModalByClass(this.modals.get(customId));
	}

	public DiscordStepChannelCreationModal getAdvancedModal(String customId) {
		final Supplier<DiscordStepChannelCreationModal> modalCreator = advancedModals.get(customId);

		if (modalCreator == null) {
			return null;
		}

		return modalCreator.get();
	}

}
