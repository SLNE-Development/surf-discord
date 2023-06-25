package dev.slne.discord.listener.interaction.button;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.button.DiscordButton;
import dev.slne.discord.discord.interaction.button.DiscordButtonManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        DiscordButtonManager buttonManager = DiscordBot.getInstance().getButtonManager();
        DiscordButton button = buttonManager.getButton(event.getButton().getId());

        if (button != null) {
            button.onClick(event);
        }
    }

}
