package dev.slne.discord.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dev.slne.discord.listener.event.Event;
import dev.slne.discord.listener.event.EventHandler;
import dev.slne.discord.listener.interaction.command.CommandReceivedListener;
import dev.slne.discord.listener.interaction.modal.DiscordModalListener;
import dev.slne.discord.listener.message.MessageCreatedListener;
import dev.slne.discord.listener.message.MessageDeletedListener;
import dev.slne.discord.listener.message.MessageUpdatedListener;
import dev.slne.discord.listener.setup.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.EventListener;

public class ListenerManager {

    private List<Listener> listeners;
    private List<EventListener> discordListeners;

    /**
     * Creates a new listener manager.
     */
    public ListenerManager() {
        this.listeners = new ArrayList<>();
        this.discordListeners = new ArrayList<>();
    }

    public void registerListeners() {
        // Currently not needed
    }

    /**
     * Registers all discord listeners.
     */
    public void registerDiscordListeners() {
        discordListeners.add(new ReadyListener());

        discordListeners.add(new CommandReceivedListener());
        discordListeners.add(new DiscordModalListener());

        discordListeners.add(new MessageCreatedListener());
        discordListeners.add(new MessageUpdatedListener());
        discordListeners.add(new MessageDeletedListener());
    }

    /**
     * Registers a listener to the jda.
     *
     * @param jda the jda
     */
    public void registerListenersToJda(JDA jda) {
        this.discordListeners.forEach(jda::addEventListener);
    }

    /**
     * Broadcasts an event to all listeners.
     *
     * @param event the event
     */
    @SuppressWarnings("java:S3776")
    public <T extends Event> void broadcastEvent(T event) {
        for (Listener listener : listeners) {
            Class<? extends Listener> eventClass = listener.getClass();
            Method[] methods = eventClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(EventHandler.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();

                    if (parameterTypes.length >= 1) {
                        Class<?> parameterType = parameterTypes[0];

                        if (parameterType.isAssignableFrom(event.getClass())) {
                            try {
                                method.invoke(listener, event);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the listeners.
     *
     * @return the listeners
     */
    public List<Listener> getListeners() {
        return listeners;
    }

}
