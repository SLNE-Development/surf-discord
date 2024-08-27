package dev.slne.discord.discord.interaction.command.commands.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.command.CommandExceptions;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.MessageManager;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.feign.dto.WhitelistDTO;
import dev.slne.discord.spring.service.whitelist.WhitelistService;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.Blocking;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Whitelist command.
 */
@DiscordCommandMeta(name = "whitelist", description = "Füge einen Spieler zur Whitelist hinzu.", permission = CommandPermission.WHITELIST)
public class WhitelistCommand extends DiscordCommand {

  private static final String USER_OPTION = "user";
  private static final String MINECRAFT_OPTION = "minecraft";
  private static final String TWITCH_OPTION = "twitch";
  private final WhitelistService whitelistService;
  private final MessageManager messageManager;

  public WhitelistCommand(WhitelistService whitelistService, MessageManager messageManager) {
    this.whitelistService = whitelistService;
    this.messageManager = messageManager;
  }

  @Override
  public @Nonnull List<OptionData> getOptions() {
    return List.of(
        new OptionData(
            OptionType.USER,
            USER_OPTION,
            RawMessages.get("interaction.command.ticket.whitelist.arg.user"),
            true
        ),
        new OptionData(
            OptionType.STRING,
            MINECRAFT_OPTION,
            RawMessages.get("interaction.command.ticket.whitelist.arg.minecraft-name"),
            true
        )
            .setRequiredRange(3, 16),
        new OptionData(
            OptionType.STRING,
            TWITCH_OPTION,
            RawMessages.get("interaction.command.ticket.whitelist.arg.twitch-name"),
            true
        )
    );
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final TextChannel channel = getTextChannelOrThrow(interaction);
    final User user = getUserOrThrow(interaction, USER_OPTION);
    final String minecraft = getStringOrThrow(interaction, MINECRAFT_OPTION,
        "Du musst einen Minecraft Namen angeben.");
    final String twitch = getStringOrThrow(interaction, TWITCH_OPTION,
        "Du musst einen Twitch Namen angeben.");
    final String discordId = user.getId();
    final User executor = interaction.getUser();

    whitelistUser(interaction, hook, user, executor, minecraft, twitch, discordId, channel);
  }

  @Async
  protected void whitelistUser(
      SlashCommandInteractionEvent interaction,
      InteractionHook hook,
      User user,
      User executor,
      String minecraft,
      String twitch,
      String discordId,
      TextChannel channel
  ) throws CommandException {
    final UUID minecraftUuid = DataApi.getUuidByPlayerName(minecraft).join();

    if (minecraftUuid == null) {
      throw CommandExceptions.MINECRAFT_USER_NOT_FOUND.create();
    }

    hook.editOriginal(RawMessages.get("interaction.command.ticket.whitelist.checking")).queue();

    final List<WhitelistDTO> whitelists = whitelistService.checkWhitelists(minecraftUuid, discordId,
            twitch)
        .join();

    if (!whitelists.isEmpty()) {
      hook.editOriginal(RawMessages.get("interaction.command.ticket.whitelist.already-whitelisted"))
          .queue();
      for (final WhitelistDTO whitelist : whitelists) {
        final MessageEmbed embed = messageManager.getWhitelistQueryEmbed(whitelist).join();
        channel.sendMessageEmbeds(embed).queue();
      }
    } else {
      hook.editOriginal(RawMessages.get("interaction.command.ticket.whitelist.adding")).queue();

      final WhitelistDTO newWhitelist = WhitelistDTO.createFrom(
          minecraftUuid,
          minecraft,
          twitch,
          user,
          executor
      );

      final WhitelistDTO createdWhitelist = whitelistService.addWhitelist(newWhitelist).join();

      if (createdWhitelist == null) {
        throw CommandExceptions.TICKET_WHITELIST.create();
      }

      addWhitelistedRole(interaction.getGuild(), user);

      final MessageEmbed embed = messageManager.getWhitelistQueryEmbed(createdWhitelist).join();
      channel.sendMessage(
              RawMessages.get("interaction.command.ticket.whitelist.added", user.getAsMention()))
          .setEmbeds(embed)
          .queue();
      hook.deleteOriginal().queue();
    }
  }

  @Blocking
  private void addWhitelistedRole(Guild guild, User user) {
    if (guild != null) {
      final GuildConfig guildConfig = GuildConfig.getConfig(guild.getId());

      if (guildConfig != null) {
        final Role whitelistedRole = guildConfig.getWhitelistedRole();

        if (whitelistedRole != null) {
          guild.addRoleToMember(user, whitelistedRole).complete();
        }
      }
    }
  }
}
