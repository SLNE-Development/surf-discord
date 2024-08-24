package dev.slne.discord.spring.annotation;

import dev.slne.discord.guild.permission.CommandPermission;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface DiscordCommandMeta {

  String name();

  String description();

  CommandPermission permission();

  boolean guildOnly() default true;

  boolean nsfw() default false;
}
