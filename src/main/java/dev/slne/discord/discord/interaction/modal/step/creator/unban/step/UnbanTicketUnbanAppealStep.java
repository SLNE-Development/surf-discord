package dev.slne.discord.discord.interaction.modal.step.creator.unban.step;

import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalStep;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class UnbanTicketUnbanAppealStep extends ModalStep {

  private static final String COMPONENT_ID = "unban-appeal";

  private String unbanAppeal;

  public UnbanTicketUnbanAppealStep(ModalStep parent) {
  }

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
    builder.addComponent(
        TextInput.create(COMPONENT_ID, "Dein Entbannungsantrag",
                TextInputStyle.PARAGRAPH)
            .setRequired(true)
            .setMinLength(300)
            .setPlaceholder(
                "Erkläre, warum du entbannt werden möchtest und was du aus deinem Verhalten gelernt hast.")
            .build()
    );
  }

  @Override
  protected void verifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    unbanAppeal = getRequiredInput(event, COMPONENT_ID);
  }

  @Override
  protected void buildOpenMessages(MessageQueue messages, TextChannel channel) {
    messages.addMessage("# Entbannungsantrag");
    messages.addMessage("> %s", unbanAppeal);
  }
}
