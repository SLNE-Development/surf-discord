package dev.slne.discord.spring.annotation.processor;

import dev.slne.discord.spring.annotation.DiscordListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Component
@DependsOn("jda")
public class DiscordListenerProcessor implements BeanPostProcessor {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("DiscordListenerProcessor");
  private final JDA jda;

  @Autowired
  public DiscordListenerProcessor(JDA jda) {
    this.jda = jda;
  }

  @Override
  public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
    final DiscordListener annotation = AnnotationUtils.findAnnotation(bean.getClass(),
        DiscordListener.class);

    if (annotation != null) {
      if (!(bean instanceof ListenerAdapter listener)) {
        throw new BeanCreationException("Bean " + beanName + " is annotated with @DiscordListener but does not extend ListenerAdapter.");
      }

      LOGGER.info("Registering listener {}", beanName);
      jda.addEventListener(listener);
    }

    return bean;
  }
}
