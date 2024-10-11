package dev.slne.discord.spring.processor

import dev.slne.discord.DiscordSpringApplication
import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.ticket.TicketType
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import java.lang.invoke.MethodHandles

@org.springframework.stereotype.Component
//@ExtensionMethod([Class::class, org.springframework.core.annotation.AnnotationUtils::class])
class ChannelCreationModalProcessor {
    private val logger = ComponentLogger.logger(ChannelCreationModalProcessor::class.java)

    init {
        val lookup = MethodHandles.lookup()
        val scanner = ClassPathScanningCandidateComponentProvider(false)

        scanner.addIncludeFilter(AnnotationTypeFilter(ChannelCreationModal::class.java))

        scanner.findCandidateComponents(DiscordSpringApplication.BASE_PACKAGE)
            .forEach { beanDefinition: BeanDefinition ->
                try {
                    val modalClass = beanDefinition.beanClassName?.let { Class.forName(it) }!!
                    check(DiscordStepChannelCreationModal::class.java.isAssignableFrom(modalClass)) { "Class annotated with @ChannelCreationModal must extend DiscordStepChannelCreationModal" }

                    val modalAnnotation: ChannelCreationModal =
                        checkNotNull(modalClass.getAnnotation(ChannelCreationModal::class.java)) { "Annotation must not be null" }

                    val constructor =
                        modalClass.getDeclaredConstructor().apply { isAccessible = true }

                    val constructorHandle = lookup.unreflectConstructor(constructor)
                    val modalId = getModalId(modalAnnotation)

                    logger.info("Registering modal {} with id {}", modalClass.simpleName, modalId)

                    DiscordModalManager.INSTANCE.registerAdvancedModal(modalId) {
                        try {
                            return@registerAdvancedModal constructorHandle.invoke() as DiscordStepChannelCreationModal
                        } catch (exception: Throwable) {
                            throw RuntimeException(exception)
                        }
                    }
                } catch (exception: ClassNotFoundException) {
                    throw RuntimeException("Class not found", exception)
                } catch (exception: NoSuchMethodException) {
                    throw RuntimeException("No constructor found", exception)
                } catch (exception: IllegalAccessException) {
                    throw RuntimeException("Illegal access", exception)
                }
            }
    }

    companion object {
        fun getModalId(annotation: ChannelCreationModal) =
            annotation.modalId.ifEmpty { annotation.ticketType.name }

        fun getTicketType(annotation: ChannelCreationModal): TicketType = annotation.ticketType
    }
}
