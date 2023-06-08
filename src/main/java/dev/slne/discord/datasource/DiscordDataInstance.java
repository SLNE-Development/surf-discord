package dev.slne.discord.datasource;

import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.instance.CoreDataInstance;
import dev.slne.data.core.pusher.packet.PusherPacket;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.datasource.database.future.CompletableDiscordFuture;
import dev.slne.discord.datasource.pusher.event.DiscordPusherEvent;

public class DiscordDataInstance extends CoreDataInstance {

    @Override
    public Path getDataPath() {
        return Path.of("data");
    }

    @Override
    public void logError(Class<?> caller, String message, Throwable... throwables) {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(caller.getSimpleName() + ",ERROR").append("] ").append(message);

        Launcher.getLogger().logError(builder.toString());
        for (Throwable throwable : throwables) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void logInfo(Class<?> caller, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(caller.getSimpleName() + ",INFO").append("] ").append(message);

        Launcher.getLogger().logInfo(builder.toString());
    }

    @Override
    public void callPusherEvent(String channelName, String pusherEventName, String userId, PusherPacket pusherPacket) {
        DiscordBot.getInstance().getListenerManager()
                .broadcastEvent(new DiscordPusherEvent<>(channelName, pusherEventName, userId, pusherPacket));
    }

    @Override
    public Class<?> getCallingClass() {
        return getClass();
    }

    @Override
    public void logWarning(Class<?> caller, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(caller.getSimpleName() + ",WARNING").append("] ").append(message);

        Launcher.getLogger().logWarn(builder.toString());
    }

    @Override
    public SurfFutureResult<Void> runAsync(Runnable runnable) {
        return CompletableDiscordFuture.runAsync(runnable);
    }

    @Override
    public SurfFutureResult<Void> runAsync(Runnable runnable, Executor executor) {
        return CompletableDiscordFuture.runAsync(runnable, executor);
    }

    @Override
    public <T> SurfFutureResult<T> supplyAsync(Supplier<T> supplier) {
        return CompletableDiscordFuture.supplyAsync(supplier);
    }

    @Override
    public <T> SurfFutureResult<T> supplyAsync(Supplier<T> supplier, Executor executor) {
        return CompletableDiscordFuture.supplyAsync(supplier, executor);
    }

}
