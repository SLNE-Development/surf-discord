package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing;

import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep;
import dev.slne.discord.message.RawMessages;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class ReportTicketGriefingStep extends ModalSelectionStep {

  private static final String OPTION_SURVIVAL = "survival";
  private static final String OPTION_EVENT = "event";

  private static final String WORLD_INPUT = "world";
  private static final String XYZ_INPUT = "x y z";
  private static final String WHAT_GRIEFED_INPUT = "what-griefed";
  private static final String ADDITIONAL_INFORMATION_INPUT = "additional-information";

  private String world, xYZ, whatGriefed, additionalInformation;

  public ReportTicketGriefingStep() {
    super(RawMessages.get("modal.report.step.selection.griefing.title"),
        SelectOption.of(
                RawMessages.get("modal.report.step.selection.griefing.survival.label"),
                OPTION_SURVIVAL
            )
            .withDescription(
                RawMessages.get("modal.report.step.selection.griefing.survival.description")
            ),
        SelectOption.of(
                RawMessages.get("modal.report.step.selection.griefing.event.label"),
                OPTION_EVENT
            )
            .withDescription(
                RawMessages.get("modal.report.step.selection.griefing.event.description")
            )
    );
  }

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
    builder.addComponent(
        TextInput.create(
                WORLD_INPUT,
                RawMessages.get("modal.report.step.griefing.input.world.label"),
                TextInputStyle.SHORT
            )
            .setRequired(true)
            .setPlaceholder(RawMessages.get("modal.report.step.griefing.input.world.placeholder"))
            .build()
    );
    builder.addComponent(
        TextInput.create(
                XYZ_INPUT,
                RawMessages.get("modal.report.step.griefing.input.xyz.label"),
                TextInputStyle.SHORT
            )
            .setRequired(true)
            .setMinLength(5)
            .setPlaceholder(RawMessages.get("modal.report.step.griefing.input.xyz.placeholder"))
            .build()
    );
    builder.addComponent(
        TextInput.create(
                WHAT_GRIEFED_INPUT,
                RawMessages.get("modal.report.step.griefing.input.what-griefed.label"),
                TextInputStyle.PARAGRAPH
            )
            .setRequired(true)
            .setPlaceholder(RawMessages.get(
                "modal.report.step.griefing.input.what-griefed.placeholder"))
            .build()
    );
    builder.addComponent(
        TextInput.create(
                ADDITIONAL_INFORMATION_INPUT,
                RawMessages.get("modal.report.step.griefing.input.additional-info.label"),
                TextInputStyle.PARAGRAPH
            )
            .setRequired(false)
            .setPlaceholder(RawMessages.get(
                "modal.report.step.griefing.input.additional-info.placeholder"))
            .build()
    );
  }

  @Override
  protected void verifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    world = getRequiredInput(event, WORLD_INPUT);
    xYZ = getRequiredInput(event, XYZ_INPUT);
    whatGriefed = getRequiredInput(event, WHAT_GRIEFED_INPUT);
    additionalInformation = getOptionalInput(event, ADDITIONAL_INFORMATION_INPUT);
  }

  @Override
  protected void buildOpenMessages(MessageQueue messages, TextChannel channel) {
    messages.addMessage(
        RawMessages.get("modal.report.step.griefing.messages.server", getSelectedServer()));
    messages.addMessage(
        RawMessages.get("modal.report.step.griefing.messages.location", xYZ, world));
    messages.addEmptyLine();
    messages.addMessage("# " + RawMessages.get(
        "modal.report.step.griefing.input.what-griefed.label"));
    messages.addMessage("> " + whatGriefed);

    if (additionalInformation != null && !additionalInformation.isBlank()) {
      messages.addEmptyLine();
      messages.addMessage("## " + RawMessages.get(
          "modal.report.step.griefing.input.additional-info.label"));
      messages.addMessage("> " + additionalInformation);
    }
  }

  private String getSelectedServer() {
    return switch (getSelected()) {
      case OPTION_SURVIVAL ->
          RawMessages.get("modal.report.step.selection.griefing.survival.label");
      case OPTION_EVENT -> RawMessages.get("modal.report.step.selection.griefing.event.label");
      default -> throw new IllegalStateException("Unexpected value: " + getSelected());
    };
  }
}
