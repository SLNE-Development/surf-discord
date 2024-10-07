package dev.slne.discord.listener.interaction.modal;

import dev.slne.discord.annotation.DiscordListener;
import dev.slne.discord.discord.interaction.modal.DiscordModal;
import dev.slne.discord.discord.interaction.modal.DiscordModalManager;
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal;
import dev.slne.discord.message.MessageManager;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

/**
 * The type Discord modal listener.
 */
@DiscordListener
public class DiscordModalListener extends ListenerAdapter {

  @Override
  public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
    final ModalInteraction interaction = event.getInteraction();
    final String modalId = interaction.getModalId();
    final DiscordModal modal = DiscordModalManager.INSTANCE.getModal(modalId);

    if (modal != null) {
      modal.execute(event);
      return;
    }

    final DiscordStepChannelCreationModal advancedModal = DiscordModalManager.INSTANCE.getAdvancedModal(
        modalId, event.getUser().getId());

    if (advancedModal != null) {
      event.deferReply(true).queue();
      CompletableFuture.runAsync(() -> advancedModal.handleUserSubmitModal(event));
      return;
    }

    event.replyEmbeds(MessageManager.getErrorEmbed(
        "Fehler",
        "Deine Aktion konnte nicht durchgeführt werden oder ist abgelaufen."
    )).setEphemeral(true).queue();
  }

}
