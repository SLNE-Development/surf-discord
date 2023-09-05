package dev.slne.discord.datasource;

import dev.slne.data.core.instance.CoreDataInstance;
import dev.slne.discord.Launcher;

import java.nio.file.Path;
import java.util.logging.Logger;

public class DiscordDataInstance extends CoreDataInstance {

    @Override
    public Path getDataPath() {
        return Path.of("data");
    }

    @Override
    public void logError(Class<?> caller, String message, Throwable... throwable) {
        Launcher.getLogger(caller).error(message);
        
        for (Throwable throwItem : throwable) {
            Logger.getLogger(caller.getName()).throwing(caller.getName(), "logError", throwItem);
        }
    }

    @Override
    public void logInfo(Class<?> caller, String message) {
        Launcher.getLogger(caller).info(message);
    }

    @Override
    public Class<?> getCallingClass() {
        return getClass();
    }

    @Override
    public void logWarning(Class<?> caller, String message) {
        Launcher.getLogger(caller).warn(message);
    }

    @Override
    public String getServerName() {
        return "Discord";
    }

    @Override
    public void exitApplication() {
        System.exit(0);
    }

    @Override
    public String getIpAddress() {
        return "127.0.0.1";
    }

    @Override
    public int getPort() {
        return 0;
    }

}
