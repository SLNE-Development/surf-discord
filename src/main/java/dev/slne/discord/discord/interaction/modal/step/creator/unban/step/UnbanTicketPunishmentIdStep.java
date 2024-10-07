package dev.slne.discord.discord.interaction.modal.step.creator.unban.step;

import dev.slne.discord.Bootstrap;
import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalStep;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.service.punishment.PunishmentService;
import feign.FeignException;
import java.util.concurrent.CompletionException;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.springframework.data.util.Lazy;

public class UnbanTicketPunishmentIdStep extends ModalStep {

  private static final ComponentLogger LOGGER = ComponentLogger.logger();
  private static final String PUNISHMENT_ID = "punishment-id";
  private static final Lazy<PunishmentService> PUNISHMENT_SERVICE = Lazy.of(
      () -> Bootstrap.getContext().getBean(
          PunishmentService.class));

  private String punishmentId;

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
    builder.addComponent(
        TextInput.create(
                PUNISHMENT_ID,
                RawMessages.get("modal.unban.step.punishment-id.input.label"),
                TextInputStyle.SHORT
            )
            .setPlaceholder(RawMessages.get("modal.unban.step.punishment-id.input.placeholder"))
            .setRequired(true)
            .setMinLength(6)
            .setMaxLength(8)
            .build()
    );
  }

  @Override
  protected void verifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    punishmentId = getRequiredInput(event, PUNISHMENT_ID);

    try {
      if (!PUNISHMENT_SERVICE.get().isValidPunishmentId(punishmentId).join()) {
        handlePunishmentNotFound();
      }
    } catch (CompletionException e) {
      if (e.getCause() instanceof FeignException.NotFound) {
        handlePunishmentNotFound();
      } else {
        LOGGER.error("Error while fetching ban by punishment ID", e);
        throw new ModalStepInputVerificationException(RawMessages.get("error.generic"));
      }
    }
  }

  @Override
  protected void buildOpenMessages(MessageQueue messages, TextChannel channel) {
    messages.addMessage(
        RawMessages.get("modal.unban.step.punishment-id.messages.punishment-id", punishmentId));
  }

  private static void handlePunishmentNotFound() throws ModalStepInputVerificationException {
    throw new ModalStepInputVerificationException(
        RawMessages.get("modal.unban.step.punishment-id.error.invalid")
    );
  }
}
