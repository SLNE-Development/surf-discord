package dev.slne.discord.listener.event.cancel;

@SuppressWarnings("unused")
public interface Cancellable {

    /**
     * Returns whether the event is cancelled.
     *
     * @return whether the event is cancelled
     */
    boolean isCancelled();

    /**
     * Sets whether the event should be cancelled.
     *
     * @param cancelled whether the event should be cancelled
     */
    void setCancelled(boolean cancelled);

}
