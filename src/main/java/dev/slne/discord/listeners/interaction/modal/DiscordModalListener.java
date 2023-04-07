package dev.slne.discord.listeners.interaction.modal;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.interaction.modal.DiscordModal;
import dev.slne.discord.interaction.modal.DiscordModalManager;
import dev.slne.discord.message.MessageManager;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

public class DiscordModalListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        ModalInteraction interaction = event.getInteraction();

        DiscordModalManager modalManager = DiscordBot.getInstance().getModalManager();
        DiscordModal modal = modalManager.getModal(interaction.getModalId());

        if (modal != null) {
            modal.execute(event);
            return;
        }

        event.replyEmbeds(MessageManager.getErrorEmbed("Fehler",
                "Deine Aktion konnte nicht durchgeführt werden oder ist abgelaufen.")).setEphemeral(true).queue();
    }

}
