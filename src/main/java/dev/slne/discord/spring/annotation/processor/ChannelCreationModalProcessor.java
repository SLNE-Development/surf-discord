package dev.slne.discord.spring.annotation.processor;

import dev.slne.discord.DiscordSpringApplication;
import dev.slne.discord.discord.interaction.modal.DiscordModalManager;
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal;
import dev.slne.discord.spring.annotation.ChannelCreationModal;
import dev.slne.discord.ticket.TicketType;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

@Component
@ExtensionMethod({Class.class, AnnotationUtils.class})
public class ChannelCreationModalProcessor {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("ChannelCreationModalProcessor");
  private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

  @Autowired
  public ChannelCreationModalProcessor(ConfigurableApplicationContext context) {
    final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
        false);
    scanner.addIncludeFilter(new AnnotationTypeFilter(ChannelCreationModal.class));

    scanner.findCandidateComponents(DiscordSpringApplication.BASE_PACKAGE)
        .forEach(beanDefinition -> {
          try {
            final Class<?> modalClass = beanDefinition.getBeanClassName().forName();
            if (!DiscordStepChannelCreationModal.class.isAssignableFrom(modalClass)) {
              throw new IllegalStateException(
                  "Class annotated with @ChannelCreationModal must extend DiscordStepChannelCreationModal");
            }

            final ChannelCreationModal modalAnnotation = modalClass.findAnnotation(
                ChannelCreationModal.class);
            assert modalAnnotation != null : "Annotation must not be null";

            final Constructor<?> constructor = modalClass.getDeclaredConstructor();
            constructor.setAccessible(true);

            final MethodHandle constructorHandle = lookup.unreflectConstructor(constructor);
            final String modalId = getModalId(modalAnnotation);

            LOGGER.info("Registering modal {} with id {}", modalClass.getSimpleName(), modalId);

            DiscordModalManager.INSTANCE.registerAdvancedModal(modalId, () -> {
              try {
                return (DiscordStepChannelCreationModal) constructorHandle.invoke();
              } catch (Throwable e) {
                throw new RuntimeException(e);
              }
            });

          } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found", e);
          } catch (NoSuchMethodException e) {
            throw new RuntimeException("No constructor found", e);
          } catch (IllegalAccessException e) {
            throw new RuntimeException("Illegal access", e);
          }
        });
  }

  public static String getModalId(ChannelCreationModal annotation) {
    final String modalId = annotation.modalId();
    return modalId.isEmpty() ? annotation.ticketType().name() : modalId;
  }

  public static TicketType getTicketType(ChannelCreationModal annotation) {
    return annotation.ticketType();
  }
}
