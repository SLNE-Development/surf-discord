package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.command.CommandExceptions;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.MessageManager;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.feign.dto.WhitelistDTO;
import dev.slne.discord.spring.service.whitelist.WhitelistService;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;

/**
 * The type WhitelistDTO query command.
 */
@DiscordCommandMeta(
    name = "wlquery",
    description = "Zeigt Whitelist Informationen über einen Benutzer an.",
    permission = CommandPermission.WHITELIST_QUERY
)
public class WhitelistQueryCommand extends DiscordCommand {

  private static final String USER_OPTION = "user";
  private static final String MINECRAFT_OPTION = "minecraft";
  private static final String TWITCH_OPTION = "twitch";
  private final WhitelistService whitelistService;
  private final MessageManager messageManager;

  public WhitelistQueryCommand(WhitelistService whitelistService, MessageManager messageManager) {
    this.whitelistService = whitelistService;
    this.messageManager = messageManager;
  }

  @Override
  public @Nonnull List<OptionData> getOptions() {
    return List.of(
        new OptionData(
            OptionType.USER,
            USER_OPTION,
            RawMessages.get("interaction.command.ticket.wlquery.arg.user"),
            false
        ),
        new OptionData(
            OptionType.STRING,
            MINECRAFT_OPTION,
            RawMessages.get("interaction.command.ticket.wlquery.arg.minecraft-name"),
            false
        ).setRequiredLength(3, 16),
        new OptionData(
            OptionType.STRING,
            TWITCH_OPTION,
            RawMessages.get("interaction.command.ticket.wlquery.arg.twitch-name"),
            false
        )
    );
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final TextChannel channel = getTextChannelOrThrow(interaction);

    final Optional<User> optionalUser = getUser(interaction, USER_OPTION);
    final Optional<String> optionalMinecraft = getString(interaction, MINECRAFT_OPTION);
    final Optional<String> optionalTwitch = getString(interaction, TWITCH_OPTION);

    if (optionalUser.isEmpty() && optionalMinecraft.isEmpty() && optionalTwitch.isEmpty()) {
      throw CommandExceptions.TICKET_WLQUERY_NO_USER.create();
    }

    final List<WhitelistDTO> whitelists = getWhitelists(
        optionalUser.orElse(null),
        optionalMinecraft.orElse(null),
        optionalTwitch.orElse(null)
    ).join();

    if (optionalUser.isPresent()) {
      messageManager.printUserWlQuery(whitelists, optionalUser.get().getName(), channel, hook);
    } else if (optionalMinecraft.isPresent()) {
      messageManager.printUserWlQuery(whitelists, optionalMinecraft.get(), channel, hook);
    } else {
      messageManager.printUserWlQuery(whitelists, optionalTwitch.get(), channel, hook);
    }
  }

  @Async
  protected CompletableFuture<List<WhitelistDTO>> getWhitelists(
      @Nullable User user,
      @Nullable String minecraft,
      @Nullable String twitch
  ) {
    final List<WhitelistDTO> whitelists;

    if (user != null) {
      whitelists = whitelistService.checkWhitelists(null, user.getId(), null).join();
    } else if (twitch != null) {
      whitelists = whitelistService.checkWhitelists(null, null, twitch).join();
    } else if (minecraft != null) {
      whitelists = DataApi.getUuidByPlayerName(minecraft)
          .thenCompose(uuid -> whitelistService.checkWhitelists(uuid, null, null)).join();
    } else {
      whitelists = null;
    }

    return CompletableFuture.completedFuture(whitelists);
  }
}
