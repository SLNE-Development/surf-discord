package dev.slne.discord.annotation;

import dev.slne.discord.ticket.TicketType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChannelCreationModal {

  String modalId() default "";

  TicketType ticketType();

}
