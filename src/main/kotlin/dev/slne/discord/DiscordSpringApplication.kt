package dev.slne.discord

import dev.slne.data.api.DataApi
import dev.slne.data.api.spring.SurfSpringApplication
import dev.slne.discord.datasource.DiscordDataInstance
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.apache.commons.lang3.ArrayUtils
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.scheduling.annotation.AsyncConfigurer

private const val BASE_PACKAGE = "dev.slne.discord"

@SurfSpringApplication(
    scanBasePackages = [BASE_PACKAGE],
    scanFeignBasePackages = ["dev.slne.discord.spring.feign.client"]
)
class DiscordSpringApplication : AsyncConfigurer {

    private val LOGGER = ComponentLogger.logger("DiscordBotApplication")

    companion object {
        @JvmStatic
        fun run(): ConfigurableApplicationContext {
            return DataApi.run(
                DiscordSpringApplication::class.java,
                DiscordDataInstance::class.java.classLoader
            )
        }
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return AsyncUncaughtExceptionHandler { throwable, method, params ->
            LOGGER.error(
                """
                   Exception message - {}
                   Method name - {}
                   ParameterValues - {}
              """.trimIndent(),
                throwable.message,
                method.name,
                ArrayUtils.toString(params)
            )
        }
    }
}