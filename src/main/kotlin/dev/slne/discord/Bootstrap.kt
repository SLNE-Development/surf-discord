package dev.slne.discord

import dev.slne.data.api.DataApi
import dev.slne.discord.message.RawMessages
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.context.ConfigurableApplicationContext

class Bootstrap {

    private val discordBot = DiscordBot()
    private lateinit var dataInstance: DiscordDataInstance

    /**
     * Method called when the launcher is loaded
     */
    fun onLoad() {
        val start = System.currentTimeMillis()

        dataInstance = DiscordDataInstance()
        DataApi(dataInstance)

        context = DiscordSpringApplication.run()

        discordBot.onLoad()
        val end = System.currentTimeMillis()

        LOGGER.info(
            String.format(
                "Done (%.3fs)! Type 'help' for a list of commands.",
                (end - start) / 1000.0
            )
        )
    }

    /**
     * Method called when the launcher is enabled
     */
    fun onEnable() {
        discordBot.onEnable()
    }

    /**
     * Method called when the launcher is disabled
     */
    fun onDisable() {
        discordBot.onDisable()
    }

    companion object {
        private val LOGGER = ComponentLogger.logger("Bootstrap")
        private lateinit var context: ConfigurableApplicationContext

        /**
         * Main method
         *
         * @param args The arguments
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val bootstrap = Bootstrap()

            LOGGER.info("Loading messages...")
            RawMessages::class.java.getClassLoader()

            bootstrap.onLoad()
            bootstrap.onEnable()
        }
    }
}
