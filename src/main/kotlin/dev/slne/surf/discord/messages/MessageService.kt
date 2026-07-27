package dev.slne.surf.discord.messages

import dev.slne.surf.discord.logger
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.io.InputStreamReader
import java.text.MessageFormat
import java.util.*

@NonNls
private const val BUNDLE = "messages"

object MessageService {
    private val messages: Object2ObjectMap<String, String> = Object2ObjectOpenHashMap()

    fun loadMessages() {
        val resource = javaClass.classLoader.getResourceAsStream("messages.properties")
            ?: error("messages.properties resource not found")

        val props = Properties()
        resource.use { stream ->
            InputStreamReader(stream, Charsets.UTF_8).use { reader ->
                props.load(reader)
            }
        }

        for ((key, value) in props) {
            messages[key.toString()] = value.toString()
        }

        logger.info("Loaded ${messages.size} messages (UTF-8).")
    }


    fun translatable(
        key: @PropertyKey(resourceBundle = BUNDLE) String,
        vararg args: Any?
    ): @Nls String {
        val template = messages[key] ?: return key

        return MessageFormat.format(template, *args)
    }
}

fun translatable(
    key: @PropertyKey(resourceBundle = BUNDLE) String,
    vararg args: String
): @Nls String = MessageService.translatable(key, *args)
