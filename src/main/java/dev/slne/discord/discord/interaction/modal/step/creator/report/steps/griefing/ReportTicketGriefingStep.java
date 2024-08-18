package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing;

import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class ReportTicketGriefingStep extends ModalSelectionStep {

  private static final SelectOption SURVIVAL = SelectOption.of("Survival", "survival");
  private static final SelectOption EVENT_1 = SelectOption.of("Event 1", "event_1");
  private static final SelectOption EVENT_2 = SelectOption.of("Event 2", "event_2");
  private static final SelectOption EVENT_3 = SelectOption.of("Event 3", "event_3");

  private static final String WORLD_INPUT = "world";
  private static final String XYZ_INPUT = "x y z";
  private static final String WHAT_GRIEFED_INPUT = "what-griefed";
  private static final String ADDITIONAL_INFORMATION_INPUT = "additional-information";

  private String world, xYZ, whatGriefed, additionalInformation;

  public ReportTicketGriefingStep() {
    super("Auf welchem Server wurdest du gegrieft?",
        SURVIVAL
            .withDescription(
                "Du wurdest auf dem Survival Server gegrieft? Dann wähle diese Option!"),
        EVENT_1
            .withDescription(
                "Du wurdest auf dem Event 1 Server gegrieft? Dann wähle diese Option!"),
        EVENT_2
            .withDescription(
                "Du wurdest auf dem Event 2 Server gegrieft? Dann wähle diese Option!"),
        EVENT_3
            .withDescription("Du wurdest auf dem Event 3 Server gegrieft? Dann wähle diese Option!")
    );
  }

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
    builder.addComponent(
        TextInput.create(WORLD_INPUT, "Welt", TextInputStyle.SHORT)
            .setRequired(true)
            .setPlaceholder("overworld")
            .build()
    );
    builder.addComponent(
        TextInput.create(XYZ_INPUT, "X Y Z", TextInputStyle.SHORT)
            .setRequired(true)
            .setMinLength(5)
            .setPlaceholder("123 64 321")
            .build()
    );
    builder.addComponent(
        TextInput.create(WHAT_GRIEFED_INPUT, "Was wurde gegrieft?", TextInputStyle.PARAGRAPH)
            .setRequired(true)
            .setPlaceholder("Beschreibe die beschädigten Strukturen oder gestohlenen Gegenstände.")
            .build()
    );
    builder.addComponent(
        TextInput.create(ADDITIONAL_INFORMATION_INPUT, "Zusätzliche Informationen",
                TextInputStyle.PARAGRAPH)
            .setRequired(false)
            .setPlaceholder("Gibt es noch etwas, das du uns mitteilen möchtest?")
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
    messages.addMessage("> Server: `%s`".formatted(getSelectedServer()));
    messages.addMessage("> Location: `%s` - `%s`".formatted(xYZ, world));
    messages.addMessage("");
    messages.addMessage("# Was wurde gegrieft?");
    messages.addMessage("> " + whatGriefed);

    if (additionalInformation != null && !additionalInformation.isBlank()) {
      messages.addMessage("");
      messages.addMessage("## Zusätzliche Informationen");
      messages.addMessage("> " + additionalInformation);
    }
  }

  private String getSelectedServer() {
    if (SURVIVAL.getValue().equals(getSelected())) {
      return SURVIVAL.getLabel();
    } else if (EVENT_1.getValue().equals(getSelected())) {
      return EVENT_1.getLabel();
    } else if (EVENT_2.getValue().equals(getSelected())) {
      return EVENT_2.getLabel();
    } else if (EVENT_3.getValue().equals(getSelected())) {
      return EVENT_3.getLabel();
    } else {
      return "Unknown";
    }
  }
}
