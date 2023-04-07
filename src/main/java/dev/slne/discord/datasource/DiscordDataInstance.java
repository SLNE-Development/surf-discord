package dev.slne.discord.datasource;

import java.nio.file.Path;

import dev.slne.data.core.instance.CoreDataInstance;

public class DiscordDataInstance extends CoreDataInstance {

    @Override
    public Path getConnectionFilePath() {
        return Path.of("data");
    }

    @Override
    public Path getDataPath() {
        return Path.of("data");
    }

    @Override
    public void logError(Class<?> caller, String message, Throwable... throwables) {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(caller.getSimpleName() + ",ERROR").append("] ").append(message);

        System.err.println(builder.toString());
        for (Throwable throwable : throwables) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void logInfo(Class<?> caller, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(caller.getSimpleName() + ",INFO").append("] ").append(message);

        System.out.println(builder.toString());
    }

}
