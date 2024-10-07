package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step;

import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep;
import dev.slne.discord.message.RawMessages;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

public class WhitelistTicketConfirmTwitchConnected extends ModalSelectionStep {

  private static final String TWITCH_CONNECT_TUTORIAL = "https://server.castcrafter.de/support.html#link-twitch";

  private static final String OPTION_YES = "yes";
  private static final String OPTION_NO = "no";

  public WhitelistTicketConfirmTwitchConnected() {
    super(
        RawMessages.get("modal.whitelist.step.twitch.question", TWITCH_CONNECT_TUTORIAL),
        SelectOption.of(
            RawMessages.get("modal.whitelist.step.twitch.question.yes"),
            OPTION_YES
        ).withEmoji(Emoji.fromUnicode("✅")),
        SelectOption.of(
            RawMessages.get("modal.whitelist.step.twitch.question.no"),
            OPTION_NO
        ).withEmoji(Emoji.fromUnicode("❌"))
    );
  }

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
  }

  @Override
  protected void verifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    if (getSelected().equals(OPTION_NO)) {
      throw new ModalStepInputVerificationException(
          RawMessages.get("modal.whitelist.step.twitch.error.not-connected"));
    }
  }
}
