package dev.slne.discord.discord.interaction.modal;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.interaction.modal.modals.UnbanTicketModal;
import dev.slne.discord.discord.interaction.modal.modals.WhitelistTicketModal;

import dev.slne.discord.discord.interaction.modal.step.creator.report.ReportTicketChannelCreationModal;
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal;
import dev.slne.discord.discord.interaction.modal.step.creator.unban.UnbanTicketChannelCreationModal;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The type Discord modal manager.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiscordModalManager {

	private final Map<String, Class<? extends DiscordModal>> modals = new ConcurrentHashMap<>();
	private final Object2ObjectMap<String, Supplier<DiscordStepChannelCreationModal>> advancedModals = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<String, DiscordStepChannelCreationModal> currentUserModals = new Object2ObjectOpenHashMap<>();

	public static final DiscordModalManager INSTANCE = new DiscordModalManager();

	{
		registerModal(WhitelistTicketModal.class);
//		registerModal(UnbanTicketModal.class);
//		registerAdvancedModal(ReportTicketChannelCreationModal::new);
//		registerAdvancedModal(UnbanTicketChannelCreationModal::new);
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

	public void registerAdvancedModal(String modalId, Supplier<DiscordStepChannelCreationModal> creator) {
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

	public DiscordStepChannelCreationModal getAdvancedModal(String customId, String userId) {
		final Supplier<DiscordStepChannelCreationModal> modalCreator = advancedModals.get(customId);

		if (modalCreator == null) {
			return null;
		}

		final DiscordStepChannelCreationModal currentModal = currentUserModals.get(userId);

		if (currentModal != null) {
			return currentModal;
		}

		return modalCreator.get();
	}

	public void setCurrentUserModal(String userId, DiscordStepChannelCreationModal modal) {
		currentUserModals.put(userId, modal);
	}
}
