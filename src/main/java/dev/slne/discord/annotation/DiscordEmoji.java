package dev.slne.discord.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordEmoji {

  String unicode() default "";

  String formatted() default "";

  DiscordCustomEmoji custom() default @DiscordCustomEmoji(id = -1, name = "", animated = false);

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface DiscordCustomEmoji {

    long id();

    String name();

    boolean animated();
  }

  class Factory {

    public static @Nullable Emoji create(DiscordEmoji discordEmoji) {
      final String unicode = discordEmoji.unicode();
      final String formatted = discordEmoji.formatted();
      final DiscordCustomEmoji custom = discordEmoji.custom();

      if (!unicode.isEmpty()) {
        return Emoji.fromUnicode(unicode);
      } else if (!formatted.isEmpty()) {
        return Emoji.fromFormatted(formatted);
      } else {
        final long id = custom.id();
        final String name = custom.name();
        final boolean animated = custom.animated();

        if (id == -1 || name.isEmpty()) {
          return null;
        }

        return Emoji.fromCustom(name, id, animated);
      }
    }
  }
}
