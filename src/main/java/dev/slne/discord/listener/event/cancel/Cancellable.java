package dev.slne.discord.listener.event.cancel;

public interface Cancellable {

    /**
     * Returns whether the event is cancelled.
     *
     * @return whether the event is cancelled
     */
    public boolean isCancelled();

    /**
     * Sets whether the event should be cancelled.
     *
     * @param cancelled whether the event should be cancelled
     */
    public void setCancelled(boolean cancelled);

}
