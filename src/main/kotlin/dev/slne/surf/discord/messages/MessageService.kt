package dev.slne.surf.discord.messages

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.logger
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import jakarta.annotation.PostConstruct
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import org.springframework.stereotype.Service
import java.io.InputStreamReader
import java.text.MessageFormat
import java.util.*

@NonNls
private const val BUNDLE = "messages"

@Service
class MessageService {
    private val messages: Object2ObjectMap<String, String> = Object2ObjectOpenHashMap()

    @PostConstruct
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
): @Nls String = getBean<MessageService>().translatable(key, *args)
