package dev.slne.discord.discord.interaction.modal.step.creator.unban.step;

import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalStep;
import dev.slne.discord.punishment.PunishmentBan;
import dev.slne.discord.punishment.PunishmentService;
import feign.FeignException;
import java.util.concurrent.CompletionException;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class UnbanTicketPunishmentIdStep extends ModalStep {

  private static final ComponentLogger LOGGER = ComponentLogger.logger();
  private static final String PUNISHMENT_ID = "punishment-id";

  private String punishmentId;

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
    builder.addComponent(
        TextInput.create(PUNISHMENT_ID, "Deine Punishment ID", TextInputStyle.SHORT)
            .setRequired(true)
            .setMinLength(6)
            .setMaxLength(8)
            .build()
    );
  }

  @Override
  protected void verifyModalInput(ModalInteractionEvent event) throws ModalStepInputVerificationException {
    punishmentId = getRequiredInput(event, PUNISHMENT_ID);

    try {
      final PunishmentBan ban = PunishmentService.INSTANCE.getBanByPunishmentId(punishmentId)
          .join();

      if (ban == null) {
        handlePunishmentNotFound();
      }
    } catch (CompletionException e) {
      if (e.getCause() instanceof FeignException.NotFound) {
        handlePunishmentNotFound();
      } else {
        LOGGER.error("Error while fetching ban by punishment ID", e);
        throw new ModalStepInputVerificationException("Es ist ein Fehler aufgetreten (NPIAFHNOAFO42676742)!");
      }
    }
  }

  @Override
  protected void buildOpenMessages(MessageQueue messages, TextChannel channel) {
    messages.addMessage("> PunishmentID: `" + punishmentId + "`");
  }

  private static void handlePunishmentNotFound() throws ModalStepInputVerificationException {
    throw new ModalStepInputVerificationException("Es konnte kein Ausschluss mit der eingegeben ID gefunden werden!");
  }
}
