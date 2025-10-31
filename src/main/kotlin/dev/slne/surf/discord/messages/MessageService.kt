package dev.slne.surf.discord.messages

import dev.slne.surf.discord.logger
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.text.MessageFormat
import java.util.*

@Service
class MessageService {
    private val messages: Object2ObjectMap<String, String> = Object2ObjectOpenHashMap()

    @PostConstruct
    fun loadMessages() {
        val resource = javaClass.classLoader.getResourceAsStream("messages.properties")
            ?: throw IllegalStateException("messages.properties not found in resources folder")

        val props = Properties()
        props.load(resource)

        for ((key, value) in props) {
            messages[key.toString()] = value.toString()
        }

        logger.info("Loaded ${messages.size} messages.")
    }

    fun translatable(key: String, vararg args: Any?): String {
        val template = messages[key] ?: return key
        return MessageFormat.format(template, *args)
    }
}
