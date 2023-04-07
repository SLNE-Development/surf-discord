package dev.slne.discord.listeners;

import java.util.ArrayList;
import java.util.List;

import dev.slne.discord.listeners.interaction.command.CommandReceivedListener;
import dev.slne.discord.listeners.interaction.modal.DiscordModalListener;
import dev.slne.discord.listeners.setup.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.EventListener;

public class ListenerManager {

    private List<EventListener> listeners;

    public ListenerManager() {
        this.listeners = new ArrayList<>();
    }

    public void registerListeners() {
        listeners.add(new ReadyListener());

        listeners.add(new CommandReceivedListener());
        listeners.add(new DiscordModalListener());
    }

    public void registerListenersToJda(JDA jda) {
        this.listeners.forEach(jda::addEventListener);
    }

}
