package dev.slne.discord.spring.annotation.processor;

import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.List;
import java.util.Optional;
import lombok.experimental.Delegate;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DiscordCommandProcessor implements BeanPostProcessor {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("DiscordCommandProcessor");
  private final Object2ObjectMap<String, DiscordCommandHolder> commands = new Object2ObjectOpenHashMap<>();

  @Override
  public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName)
      throws BeansException {
    final DiscordCommandMeta annotation = AnnotationUtils.findAnnotation(bean.getClass(),
        DiscordCommandMeta.class);

    if (annotation != null) {
      if (!(bean instanceof DiscordCommand command)) {
        throw new BeanCreationException("Bean " + beanName
            + " is annotated with @DiscordCommandMeta but does not extend DiscordCommand.");
      }

      final DiscordCommandHolder holder = new DiscordCommandHolder(annotation, command);
      commands.put(annotation.name(), holder);

      LOGGER.info("Found command {} with name {}", beanName, annotation.name());
    }

    return bean;
  }

  public Optional<DiscordCommandHolder> getCommand(@NotNull String name) {
    return Optional.ofNullable(commands.get(name));
  }

  @Async
  public void updateCommands(@NotNull Guild guild) {
    LOGGER.info("Starting to update commands for guild {} ({})", guild.getName(), guild.getId());

    final ObjectList<CommandData> commandDatas = new ObjectArrayList<>(commands.values().size());

    for (final DiscordCommandHolder commandHolder : commands.values()) {
      commandDatas.add(Commands.slash(commandHolder.name(), commandHolder.description())
              .setGuildOnly(commandHolder.guildOnly())
              .setNSFW(commandHolder.nsfw())
              .addSubcommands(commandHolder.command().getSubCommands())
              .addOptions(commandHolder.command().getOptions())
//          .setDefaultPermissions(commandHolder.command().getDefaultMemberPermissions()) // TODO: 24.08.2024 10:11 - why not in use?
      );
    }

    final List<String> updatedCommandNames = guild.updateCommands().addCommands(commandDatas)
        .complete()
        .stream()
        .map(Command::getName)
        .toList();

    LOGGER.info(
        "Updated {} commands for guild {} ({}) with names: {}",
        updatedCommandNames.size(),
        guild.getName(),
        guild.getId(),
        updatedCommandNames
    );
  }

  public record DiscordCommandHolder(@Delegate DiscordCommandMeta meta, DiscordCommand command) {

  }
}
