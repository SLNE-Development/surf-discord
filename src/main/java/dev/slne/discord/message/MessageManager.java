package dev.slne.discord.message;

import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.extensions.MemberExtensions;
import dev.slne.discord.spring.feign.dto.WhitelistDTO;
import dev.slne.discord.spring.service.whitelist.WhitelistService;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.util.TimeUtils;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * The type Message manager.
 */
@Service
@ExtensionMethod({MemberExtensions.class})
public class MessageManager {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("MessageManager");
  private final EmbedManager embedManager;
  private final JDA jda;
  private final WhitelistService whitelistService;

  /**
   * Private constructor to prevent instantiation.
   */
  private MessageManager(EmbedManager embedManager, JDA jda, WhitelistService whitelistService) {
    this.embedManager = embedManager;
    this.jda = jda;
    this.whitelistService = whitelistService;
  }

  /**
   * Returns an error MessageEmbed with the given title and description.
   *
   * @param title       The title of the embed.
   * @param description The description of the embed.
   * @return The MessageEmbed.
   */
  public static @Nonnull MessageEmbed getErrorEmbed(String title, String description) {
    EmbedBuilder embedBuilder = new EmbedBuilder();

    embedBuilder.setTitle(title);
    embedBuilder.setDescription(description);
    embedBuilder.setColor(EmbedColors.ERROR_COLOR);
    embedBuilder.setTimestamp(TimeUtils.berlinTimeProvider().getCurrentTime());

    return embedBuilder.build();
  }

  @Async
  public CompletableFuture<Void> sendTicketClosedMessages(Ticket ticket) {
    final MessageEmbed closeEmbed = embedManager.buildTicketClosedEmbed(ticket).join();

    if (closeEmbed == null) {
      return CompletableFuture.completedFuture(null);
    }

    final User author = ticket.getTicketAuthorNow();
    final Guild guild = ticket.getGuild();

    if (guild == null) {
      return CompletableFuture.completedFuture(null);
    }

    final GuildConfig guildConfig = GuildConfig.getByGuild(guild);

    if (guildConfig == null) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.completedFuture(CompletableFuture.allOf(ticket.getMembers().stream()
        .filter(TicketMember::isActivated)
        .map(ticketMember -> sendTicketClosedMessage(closeEmbed, ticketMember, author, guild,
            guildConfig))
        .toArray(CompletableFuture[]::new)).join());
  }

  @Async
  protected CompletableFuture<Void> sendTicketClosedMessage(
      MessageEmbed closedEmbed,
      @NotNull TicketMember receiver,
      User author,
      Guild guild,
      GuildConfig guildConfig
  ) {
    final User receiverUser = receiver.getMemberNow();

    if (receiverUser == null || receiverUser.equals(jda.getSelfUser())) {
      return CompletableFuture.completedFuture(null);
    }

    final boolean isAuthor = receiverUser.equals(author);
    final Member receiverMember = guild.retrieveMember(receiverUser).complete();
    final boolean isTeamMember = receiverMember.isTeamMember(guildConfig);

    if (!isAuthor && isTeamMember) {
      return CompletableFuture.completedFuture(null);
    }

    try {
      receiverUser.openPrivateChannel()
          .flatMap(channel -> channel.sendMessageEmbeds(closedEmbed))
          .complete();
    } catch (ErrorResponseException e) {
      if (e.getErrorResponse().equals(ErrorResponse.CANNOT_SEND_TO_USER)) {
        return CompletableFuture.completedFuture(null);
      }

      LOGGER.error("Failed to send ticket closed message to user %s".formatted(receiverUser), e);
    }

    return CompletableFuture.completedFuture(null);
  }

  @Async
  public void printUserWlQuery(User user, TextChannel channel) {
    channel.sendTyping().queue();
    final List<WhitelistDTO> whitelists = whitelistService.checkWhitelists(null, user.getId(), null)
        .join();

    try {
      printUserWlQuery(whitelists, user.getName(), channel, null);
    } catch (CommandException e) {
      channel.sendMessage(e.getMessage()).queue();
    }
  }

  public void printUserWlQuery(List<WhitelistDTO> whitelists, String name, TextChannel channel,
      @Nullable InteractionHook hook)
      throws CommandException {
    if (whitelists.isEmpty()) {
      throw new CommandException(
          "Es wurden keine Whitelist Einträge für \"" + name + "\" gefunden.");
    }

    printWlQuery(channel, "\"" + name + "\"", whitelists);

    if (hook != null) {
      hook.deleteOriginal().queue();
    }
  }

  @Async
  public void printWlQuery(TextChannel channel, String title, List<WhitelistDTO> whitelists) {
    title = title.replace("\"", "");
    channel.sendMessage("WlQuery für: `" + title + "`").queue();

    for (final WhitelistDTO whitelist : whitelists) {
      final MessageEmbed embed = getWhitelistQueryEmbed(whitelist).join();
      channel.sendMessageEmbeds(embed).queue();
    }
  }

  @Async
  public CompletableFuture<MessageEmbed> getWhitelistQueryEmbed(@NotNull WhitelistDTO whitelist) {
    final EmbedBuilder builder = new EmbedBuilder()
        .setTitle("Whitelist Query")
        .setFooter("Whitelist Query", jda.getSelfUser().getAvatarUrl())
        .setDescription("Whitelist Informationen")
        .setColor(EmbedColors.WL_QUERY)
        .setTimestamp(ZonedDateTime.now(TimeFormatter.EUROPE_BERLIN));

    final String minecraftName = whitelist.getMinecraftName();
    final String twitchLink = whitelist.getTwitchLink();
    final UUID uuid = whitelist.getUuid();
    final User discordUser = whitelist.getDiscordUserNow();
    final User addedBy = whitelist.getAddedByNow();

    if (minecraftName != null) {
      builder.addField("Minecraft Name", minecraftName, true);
    }

    if (twitchLink != null) {
      builder.addField("Twitch Link", twitchLink, true);
    }

    if (discordUser != null) {
      builder.addField("Discord User", discordUser.getAsMention(), true);
    }

    if (addedBy != null) {
      builder.addField("Added By", addedBy.getAsMention(), true);
    }

    builder.addField("UUID", uuid.toString(), false);

    return CompletableFuture.completedFuture(builder.build());
  }
}
