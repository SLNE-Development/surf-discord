package dev.slne.discord.listener.event;

public abstract class Event {

    private final boolean async;

    /**
     * Creates a new event.
     */
    protected Event() {
        this(false);
    }

    /**
     * Creates a new event.
     *
     * @param async whether the event should be executed asynchronously
     */
    protected Event(boolean async) {
        this.async = async;
    }

    /**
     * Returns whether the event should be executed asynchronously.
     *
     * @return whether the event should be executed asynchronously
     */
    @SuppressWarnings("unused")
    public boolean isAsync() {
        return async;
    }

}
