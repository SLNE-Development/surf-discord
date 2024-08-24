package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import dev.slne.discord.spring.feign.dto.WhitelistDTO;
import dev.slne.discord.spring.service.whitelist.WhitelistService;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

  public WhitelistQueryCommand(WhitelistService whitelistService) {
    this.whitelistService = whitelistService;
  }

  @Override
  public @Nonnull List<OptionData> getOptions() {
    return List.of(
        new OptionData(
            OptionType.USER,
            USER_OPTION,
            "Der Benutzer über den Informationen angezeigt werden sollen.",
            false
        ),
        new OptionData(
            OptionType.STRING,
            MINECRAFT_OPTION,
            "Der Minecraft Name des Benutzers.",
            false
        ).setRequiredLength(3, 16),
        new OptionData(
            OptionType.STRING,
            TWITCH_OPTION,
            "Der Twitch Name des Benutzers.",
            false
        )
    );
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    if (!(interaction.getChannel() instanceof TextChannel channel)) {
      throw new CommandException("Dieser Befehl kann nur in Textkanälen verwendet werden.");
    }

    final Optional<User> optionalUser = getUser(interaction, USER_OPTION);
    final Optional<String> optionalMinecraft = getString(interaction, MINECRAFT_OPTION);
    final Optional<String> optionalTwitch = getString(interaction, TWITCH_OPTION);

    if (optionalUser.isEmpty() && optionalMinecraft.isEmpty() && optionalTwitch.isEmpty()) {
      throw new CommandException("Es wurde kein Benutzer angegeben.");
    }

    final List<WhitelistDTO> whitelists = getWhitelists(
        optionalUser.orElse(null),
        optionalMinecraft.orElse(null),
        optionalTwitch.orElse(null)
    ).join();

    if (optionalUser.isPresent()) {
      printUserWlQuery(whitelists, optionalUser.get().getName(), channel, hook);
    } else if (optionalMinecraft.isPresent()) {
      printUserWlQuery(whitelists, optionalMinecraft.get(), channel, hook);
    } else {
      printUserWlQuery(whitelists, optionalTwitch.get(), channel, hook);
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

  protected void printUserWlQuery(List<WhitelistDTO> whitelists, String name, TextChannel channel,
      InteractionHook hook)
      throws CommandException {
    if (whitelists.isEmpty()) {
      throw new CommandException(
          "Es wurden keine Whitelist Einträge für \"" + name + "\" gefunden.");
    }

    printWlQuery(channel, "\"" + name + "\"", whitelists);
    hook.deleteOriginal().queue();
  }

  /**
   * Prints a wlquery request.
   *
   * @param channel    The channel.
   * @param title      The title.
   * @param whitelists The whitelists.
   */
  @Async
  public void printWlQuery(TextChannel channel, String title, List<WhitelistDTO> whitelists) {
    title = title.replace("\"", "");
    channel.sendMessage("WlQuery für: `" + title + "`").queue();

    for (final WhitelistDTO whitelist : whitelists) {
      final MessageEmbed embed = whitelistService.getWhitelistQueryEmbed(whitelist).join();
      channel.sendMessageEmbeds(embed).queue();
    }
  }
}
