package dev.slne.discord.discord.interaction.modal.step.creator.unban.step;

import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalStep;
import dev.slne.discord.message.RawMessages;
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
        TextInput.create(
                COMPONENT_ID,
                RawMessages.get("modal.unban.step.appeal.input.appeal.label"),
                TextInputStyle.PARAGRAPH
            )
            .setRequired(true)
            .setMinLength(300)
            .setPlaceholder(
                RawMessages.get("modal.unban.step.appeal.input.appeal.placeholder")
            )
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
    messages.addMessage(RawMessages.get("modal.unban.step.appeal.messages.appeal.title"));
    messages.addMessage(RawMessages.get("modal.unban.step.appeal.messages.appeal", unbanAppeal));
  }
}
