package dev.slne.discord.datasource;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import dev.slne.data.core.file.JsonFileReader;

public class BotConnectionFile extends JsonFileReader {

    public BotConnectionFile() throws FileNotFoundException {
        super(Path.of("data"), "bot-connection.json");
    }

}
