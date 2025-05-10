package dev.slne.discord.message

import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.MessageFormat
import java.util.*

private const val BUNDLE_NAME = "messages"
private const val BUNDLE_PATH = "$BUNDLE_NAME.properties"

object RawMessages {
    private val properties = Properties()
    private val logger = ComponentLogger.logger()

    init {
        try {
            javaClass.classLoader
                .getResourceAsStream(BUNDLE_PATH).use { defaultStream ->
                    if (defaultStream != null) {
                        properties.load(InputStreamReader(defaultStream, StandardCharsets.UTF_8))
                    } else {
                        throw FileNotFoundException("Default properties file not found")
                    }
                }
        } catch (e: IOException) {
            logger.error("Error while loading default properties", e)
        }
    }

    fun getMessage(
        key: @NonNls @PropertyKey(resourceBundle = BUNDLE_NAME) String?,
        vararg params: Any?
    ): @Nls String {
        val message = properties.getProperty(key) ?: return "Message key not found"

        return MessageFormat.format(message, *params)
    }
}

fun translatable(
    key: @NonNls @PropertyKey(resourceBundle = BUNDLE_NAME) String,
    vararg params: Any?
): String {
    return RawMessages.getMessage(key, *params)
}
