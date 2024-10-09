package dev.slne.discord.message

import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.io.*
import java.text.MessageFormat
import java.util.*

class RawMessages private constructor() {
    private var properties: Properties

    init {
        properties = Properties()

        try {
            javaClass.classLoader
                .getResourceAsStream(BUNDLE_PATH).use { defaultStream ->
                    if (defaultStream != null) {
                        properties.load(defaultStream)
                    } else {
                        throw FileNotFoundException("Default properties file not found")
                    }
                }
        } catch (e: IOException) {
            LOGGER.error("Error while loading default properties", e)
        }

        val file = File(TARGET_PATH)
        if (file.exists()) {
            try {
                FileInputStream(file).use { inputStream ->
                    val existingProperties = Properties()
                    existingProperties.load(inputStream)

                    var updated = false
                    for (key in properties.stringPropertyNames()) {
                        if (!existingProperties.containsKey(key)) {
                            existingProperties.setProperty(key, properties.getProperty(key))
                            updated = true
                        }
                    }

                    properties = existingProperties
                    if (updated) {
                        saveProperties()
                    }
                }
            } catch (e: IOException) {
                LOGGER.error("Error while loading existing properties", e)
            }
        } else {
            try {
                val parentFile = file.parentFile

                if (parentFile != null) {
                    if (!parentFile.mkdirs()) {
                        LOGGER.error("Error while creating parent directories")
                    }
                }

                if (!file.createNewFile()) {
                    LOGGER.error("Error while creating properties file")
                }

                saveProperties()
            } catch (e: IOException) {
                LOGGER.error("Error while creating properties file", e)
            }
        }
    }

    fun getMessage(
        key: @NonNls @PropertyKey(resourceBundle = BUNDLE_NAME) String?,
        vararg params: Any?
    ): @Nls String {
        val message = properties.getProperty(key) ?: return "Message key not found"

        return MessageFormat.format(message, *params)
    }

    private fun saveProperties() {
        try {
            FileOutputStream(TARGET_PATH).use { outputStream ->
                properties.store(outputStream, "Updated properties")
            }
        } catch (e: IOException) {
            LOGGER.error("Error while saving properties", e)
        }
    }

    companion object {
        private val LOGGER = ComponentLogger.logger("RawMessages")
        private const val BUNDLE_NAME = "messages"
        private const val BUNDLE_PATH = BUNDLE_NAME + ".properties"
        private const val TARGET_PATH = "messages/messages.properties"

        private val INSTANCE = RawMessages()

        @JvmStatic
        fun get(
            key: @NonNls @PropertyKey(resourceBundle = BUNDLE_NAME) String?,
            vararg params: Any?
        ): @Nls String? {
            return INSTANCE.getMessage(key, *params)
        }
    }
}
