package dev.slne.discord.listener.interaction.button;

import dev.slne.discord.spring.annotation.DiscordListener;
import dev.slne.discord.spring.annotation.processor.DiscordButtonProcessor;
import dev.slne.discord.spring.annotation.processor.DiscordButtonProcessor.DiscordButtonHandlerHolder;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * The type Discord button listener.
 */
@DiscordListener
public class DiscordButtonListener extends ListenerAdapter {

  private final DiscordButtonProcessor discordButtonProcessor;

  public DiscordButtonListener(DiscordButtonProcessor discordButtonProcessor) {
    this.discordButtonProcessor = discordButtonProcessor;
  }

  @Override
  public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
    final DiscordButtonHandlerHolder holder = discordButtonProcessor.getHandler(
        event.getButton().getId());

    if (holder != null) {
      holder.handler().onClick(event);
    }
  }
}
