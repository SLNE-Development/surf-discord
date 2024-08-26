package dev.slne.discord.spring.processor;

import dev.slne.discord.annotation.DiscordButton;
import dev.slne.discord.annotation.DiscordEmoji;
import dev.slne.discord.discord.interaction.button.DiscordButtonHandler;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.experimental.Delegate;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Component
public class DiscordButtonProcessor implements BeanPostProcessor {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("DiscordButtonProcessor");
  private final Object2ObjectMap<String, DiscordButtonHandlerHolder> handlers = new Object2ObjectOpenHashMap<>();

  @Override
  public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName)
      throws BeansException {
    final DiscordButton annotation = AnnotationUtils.findAnnotation(bean.getClass(),
        DiscordButton.class);

    if (annotation != null) {
      if (!(bean instanceof DiscordButtonHandler button)) {
        throw new BeanCreationException("Bean " + beanName
                                        + " is annotated with @DiscordButton but does not implement DiscordButtonHandler.");
      }

      final DiscordButtonHandlerHolder holder = new DiscordButtonHandlerHolder(annotation, button);
      handlers.put(holder.id(), holder);

      LOGGER.info("Found button handler {} with id {}", beanName, holder.id());
    }

    return bean;
  }

  public @Nullable DiscordButtonHandlerHolder getHandler(String id) {
    return handlers.get(id);
  }

  @Getter
  public record DiscordButtonHandlerHolder(@Delegate DiscordButton annotation,
                                           DiscordButtonHandler handler) {

    public @NotNull Button toJdaButton() {
      return Button.of(
          style(),
          id(),
          label(),
          DiscordEmoji.Factory.create(emoji())
      );
    }
  }
}
