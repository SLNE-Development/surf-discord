package dev.slne.discord.datasource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import dev.slne.data.core.DataSource;
import dev.slne.data.core.instance.DataApi;
import dev.slne.discord.Launcher;

public class DiscordDataSource extends DataSource {

    private static DiscordDataSource instance;
    private DiscordDataInstance dataInstance;
    private DataApi dataApi;

    @Override
    public void onLoad() {
        instance = this;
        dataInstance = new DiscordDataInstance();

        dataApi = new DataApi(dataInstance);
    }

    @Override
    public void onEnable() {
        dataInstance.ignite();
    }

    @Override
    public void onDisable() {
        DataApi.teardown();
    }

    public void attachLibraries() {
        Path libraryPath = instance.getDataInstance().getDataPath().resolve("../libraries");

        // List all files in the library directory
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(libraryPath)) {
            for (Path path : directoryStream) {
                if (path.toString().endsWith(".jar")) {
                    attachLibraryToClassPath(path.toFile());

                    Launcher.getLogger().logInfo("Injected " + path.getFileName());
                }
            }
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private void attachLibraryToClassPath(File toAdd) throws MalformedURLException, ClassNotFoundException {
        // Load all classes which are contained in the given jar file
        try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { toAdd.toURI().toURL() },
                getClass().getClassLoader())) {
            for (File file : toAdd.listFiles()) {
                if (file.isDirectory()) {
                    attachLibraryToClassPath(file);
                } else if (file.getName().endsWith(".class")) {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    className = className.replace(File.separatorChar, '.');

                    classLoader.loadClass(className);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static DiscordDataSource getInstance() {
        return instance;
    }

    public DiscordDataInstance getDataInstance() {
        return dataInstance;
    }

    public DataApi getDataApi() {
        return dataApi;
    }

}
