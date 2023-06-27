package dev.slne.discord.listener.interaction.command;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.discord.interaction.command.DiscordCommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class CommandReceivedListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        SlashCommandInteraction interaction = event.getInteraction();

        DiscordCommandManager commandManager = DiscordBot.getInstance().getCommandManager();
        DiscordCommand command = commandManager.findCommand(interaction.getName());

        if (command != null) {
            command.internalExecute(event);
        }
    }

}
