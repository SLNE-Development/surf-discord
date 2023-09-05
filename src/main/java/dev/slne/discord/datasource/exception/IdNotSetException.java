package dev.slne.discord.datasource.exception;

@SuppressWarnings("unused")
public class IdNotSetException extends Exception {

    /**
     * Constructs a new {@link IdNotSetException}
     */
    public IdNotSetException() {
        super();
    }

    /**
     * Constructs a new {@link IdNotSetException}
     *
     * @param message the message
     */
    public IdNotSetException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link IdNotSetException}
     *
     * @param message the message
     * @param cause   the cause
     */
    public IdNotSetException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@link IdNotSetException}
     *
     * @param cause the cause
     */
    public IdNotSetException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@link IdNotSetException}
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  whether suppression is enabled
     * @param writableStackTrace whether the stack trace is writable
     */
    public IdNotSetException(String message, Throwable cause, boolean enableSuppression,
                             boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
