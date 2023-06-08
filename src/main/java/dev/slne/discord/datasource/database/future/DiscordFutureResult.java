package dev.slne.discord.datasource.database.future;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import dev.slne.data.core.database.future.SurfFutureResult;

public class DiscordFutureResult<T> extends SurfFutureResult<T> {

    /**
     * Creates a new future result.
     *
     * @param future the future
     */
    public DiscordFutureResult(CompletableFuture<T> future) {
        super(future);
    }

    /**
     * Creates a new future result.
     *
     * @param <T>    the type of the future result
     * @param future the future
     * @return the future result
     */
    public static <T> DiscordFutureResult<T> of(CompletableFuture<T> future) {
        return new DiscordFutureResult<>(future);
    }

    @Override
    public void whenComplete(@NotNull Consumer<? super T> callback, Consumer<Throwable> throwableConsumer) {
        this.getFuture().thenAcceptAsync(callback).exceptionally(throwable -> {
            throwableConsumer.accept(throwable);
            return null;
        });
    }

}
