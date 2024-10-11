package dev.slne.discord.spring.processor

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.DependsOn
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component

@Component
@DependsOn("jda")
class DiscordListenerProcessor @Autowired constructor(private val jda: JDA) : BeanPostProcessor {
    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val annotation = AnnotationUtils.findAnnotation(
            bean.javaClass,
            DiscordListener::class.java
        )

        if (annotation != null) {
            if (bean !is ListenerAdapter) {
                throw BeanCreationException(
                    ("Bean " + beanName
                            + " is annotated with @DiscordListener but does not extend ListenerAdapter.")
                )
            }

            LOGGER.info("Registering listener {}", beanName)
            jda.addEventListener(bean)
        }

        return bean
    }

    companion object {
        private val LOGGER = ComponentLogger.logger("DiscordListenerProcessor")
    }
}
