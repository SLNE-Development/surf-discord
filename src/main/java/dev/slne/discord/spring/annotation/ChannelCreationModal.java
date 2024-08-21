package dev.slne.discord.spring.annotation;

import dev.slne.discord.ticket.TicketType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ChannelCreationModal {

  String modalId() default "";

  TicketType ticketType();

}
