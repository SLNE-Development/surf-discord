package dev.slne.discord.spring.annotation;

import dev.slne.discord.annotation.DiscordEmoji;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface DiscordButton {

  String id();

  String label();

  ButtonStyle style();

  DiscordEmoji emoji() default @DiscordEmoji;
}
