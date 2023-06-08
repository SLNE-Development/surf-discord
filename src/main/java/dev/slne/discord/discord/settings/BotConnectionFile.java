package dev.slne.discord.discord.settings;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import dev.slne.data.core.config.JsonConfigReader;

public class BotConnectionFile extends JsonConfigReader {

    /**
     * Creates a new bot connection file.
     *
     * @throws FileNotFoundException if the file could not be found
     */
    public BotConnectionFile() throws FileNotFoundException {
        super(Path.of("data"), "bot-connection.json");
    }

}
