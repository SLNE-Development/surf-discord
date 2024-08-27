package dev.slne.discord.util;

import dev.slne.discord.exception.command.CommandException;

@FunctionalInterface
public interface ExceptionFactory<T extends Throwable> {

  T create();

  @FunctionalInterface
  interface CommandExceptionFactory extends ExceptionFactory<CommandException> {

  }

  @FunctionalInterface
  interface ExceptionFactory1<A, T extends Throwable> {

    T create(A a);

    @FunctionalInterface
    interface CommandExceptionFactory1<A> extends ExceptionFactory1<A, CommandException> {

    }
  }
}