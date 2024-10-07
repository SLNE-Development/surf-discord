package dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step;

import dev.slne.data.api.DataApi;
import dev.slne.discord.Bootstrap;
import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalStep;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.service.whitelist.WhitelistService;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.springframework.data.util.Lazy;

public class WhitelistTicketMinecraftNameStep extends ModalStep {

  private static final Lazy<WhitelistService> WHITELIST_SERVICE = Lazy.of(
      () -> Bootstrap.getContext().getBean(
          WhitelistService.class));
  private static final String MINECRAFT_NAME = "minecraft-name";

  private String minecraftName;

  public WhitelistTicketMinecraftNameStep(ModalStep parent) {
  }

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
    builder.addComponent(
        TextInput.create(
                MINECRAFT_NAME,
                RawMessages.get("modal.whitelist.step.minecraft.label"),
                TextInputStyle.SHORT
            )
            .setRequired(true)
            .setRequiredRange(3, 16)
            .build()
    );
  }

  @Override
  protected void verifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    this.minecraftName = getRequiredInput(event, MINECRAFT_NAME);
    final UUID uuid;

    try {
      uuid = DataApi.getUuidByPlayerName(this.minecraftName).join();
    } catch (CompletionException e) {
      throw new ModalStepInputVerificationException(
          RawMessages.get("modal.whitelist.step.minecraft.invalid"), e);
    }

    if (WHITELIST_SERVICE.get().isWhitelisted(uuid, null, null).join()) {
      throw new ModalStepInputVerificationException(
          RawMessages.get("interaction.command.ticket.whitelist.already-whitelisted"));
    }
  }

  @Override
  protected void buildOpenMessages(MessageQueue messages, TextChannel channel) {
    messages.addMessage(RawMessages.get("modal.whitelist.step.minecraft.open", this.minecraftName));
  }
}
