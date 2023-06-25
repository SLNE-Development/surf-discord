package dev.slne.discord.discord.interaction.modal;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DiscordModalManager {

    private Map<String, Class<? extends DiscordModal>> modals;

    /**
     * Create a new modal manager
     */
    public DiscordModalManager() {
        this.modals = new HashMap<>();
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

    /**
     * Get the modal by the class
     *
     * @param clazz The class of the modal
     * @return DiscordModal
     * @throws IllegalArgumentException If the class doesn't have a constructor
     */
    private DiscordModal getModalByClass(Class<? extends DiscordModal> clazz) throws IllegalArgumentException {
        DiscordModal discordModal = null;

        if (clazz == null) {
            return null;
        }

        try {
            discordModal = (DiscordModal) clazz.getDeclaredConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | SecurityException exception) {
            exception.printStackTrace();
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
     * @return DiscordModal
     */
    public DiscordModal getModal(String customId) {
        return getModalByClass(this.modals.get(customId));
    }

    /**
     * Get the map of modals
     *
     * @return Map<String, Class<? extends DiscordModal>>
     */
    public Map<String, Class<? extends DiscordModal>> getModals() {
        return modals;
    }

}
