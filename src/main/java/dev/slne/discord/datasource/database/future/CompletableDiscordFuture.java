package dev.slne.discord.datasource.database.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class CompletableDiscordFuture {

    /**
     * Creates a new completable discord future.
     */
    private CompletableDiscordFuture() {
    }

    /**
     * Supply a future result.
     *
     * @param <T>      the type of the future result
     * @param supplier the supplier
     * @return the future result
     */
    public static <T> DiscordFutureResult<T> supplyAsync(Supplier<T> supplier) {
        return DiscordFutureResult.of(CompletableFuture.supplyAsync(supplier));
    }

    /**
     * Supply a future result.
     *
     * @param <T>      the type of the future result
     * @param supplier the supplier
     * @param executor the executor
     * @return the future result
     */
    public static <T> DiscordFutureResult<T> supplyAsync(Supplier<T> supplier, Executor executor) {
        return DiscordFutureResult.of(CompletableFuture.supplyAsync(supplier, executor));
    }

    /**
     * Run a future result.
     *
     * @param supplier the supplier
     * @return the future result
     */
    public static DiscordFutureResult<Void> runAsync(Runnable supplier) {
        return DiscordFutureResult.of(CompletableFuture.runAsync(supplier));
    }

    /**
     * Run a future result.
     *
     * @param supplier the supplier
     * @param executor the executor
     * @return the future result
     */
    public static DiscordFutureResult<Void> runAsync(Runnable supplier, Executor executor) {
        return DiscordFutureResult.of(CompletableFuture.runAsync(supplier, executor));
    }

}
