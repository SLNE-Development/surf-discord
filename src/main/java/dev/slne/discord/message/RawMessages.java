package dev.slne.discord.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

public class RawMessages {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("RawMessages");
  private static final String BUNDLE_NAME = "messages";
  private static final String BUNDLE_PATH = BUNDLE_NAME + ".properties";
  private static final String TARGET_PATH = "messages/messages.properties";

  private static final RawMessages INSTANCE = new RawMessages();

  private Properties properties;

  private RawMessages() {
    properties = new Properties();

    try (final InputStream defaultStream = getClass().getClassLoader()
        .getResourceAsStream(BUNDLE_PATH)) {
      if (defaultStream != null) {
        properties.load(defaultStream);
      } else {
        throw new FileNotFoundException("Default properties file not found");
      }
    } catch (IOException e) {
      LOGGER.error("Error while loading default properties", e);
    }

    final File file = new File(TARGET_PATH);
    if (file.exists()) {
      try (final FileInputStream inputStream = new FileInputStream(file)) {
        final Properties existingProperties = new Properties();
        existingProperties.load(inputStream);

        boolean updated = false;
        for (final String key : properties.stringPropertyNames()) {
          if (!existingProperties.containsKey(key)) {
            existingProperties.setProperty(key, properties.getProperty(key));
            updated = true;
          }
        }

        properties = existingProperties;

        if (updated) {
          saveProperties();
        }

      } catch (IOException e) {
        LOGGER.error("Error while loading existing properties", e);
      }
    } else {
      try {
        final File parentFile = file.getParentFile();

        if (parentFile != null) {
          if (!parentFile.mkdirs()) {
            LOGGER.error("Error while creating parent directories");
          }
        }

        if (!file.createNewFile()) {
          LOGGER.error("Error while creating properties file");
        }

        saveProperties();
      } catch (IOException e) {
        LOGGER.error("Error while creating properties file", e);
      }
    }
  }

  public @Nls String getMessage(
      @NonNls @PropertyKey(resourceBundle = BUNDLE_NAME) String key,
      Object... params
  ) {
    final String message = properties.getProperty(key);
    if (message == null) {
      return "Message key not found";
    }

    return MessageFormat.format(message, params);
  }

  public static @Nls String get(
      @NonNls @PropertyKey(resourceBundle = BUNDLE_NAME) String key,
      Object... params
  ) {
    return INSTANCE.getMessage(key, params);
  }

  private void saveProperties() {
    try (final FileOutputStream outputStream = new FileOutputStream(TARGET_PATH)) {
      properties.store(outputStream, "Updated properties");
    } catch (IOException e) {
      LOGGER.error("Error while saving properties", e);
    }
  }
}
