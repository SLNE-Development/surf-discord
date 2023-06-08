package dev.slne.discord.discord.interaction.modal;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import dev.slne.discord.ticket.modals.TicketModal;

public class DiscordModalManager {

    private Map<String, Class<? extends DiscordModal>> modals;

    public DiscordModalManager() {
        this.modals = new HashMap<>();

        this.registerModal(TicketModal.WhitelistTicketModal.class);
        this.registerModal(TicketModal.ServerSupportTicketModal.class);
        this.registerModal(TicketModal.DiscordSupportTicketModal.class);
        this.registerModal(TicketModal.BugReportTicketModal.class);
    }

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

    public DiscordModal getModal(String customId) {
        return getModalByClass(this.modals.get(customId));
    }

    public Map<String, Class<? extends DiscordModal>> getModals() {
        return modals;
    }

}
