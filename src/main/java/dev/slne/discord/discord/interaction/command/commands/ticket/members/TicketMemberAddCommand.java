package dev.slne.discord.discord.interaction.command.commands.ticket.members;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.ticket.member.TicketAddMemberException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.EmbedColors;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.TicketChannelHelper;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.util.TimeUtils;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Ticket member add command.
 */
@DiscordCommandMeta(name = "add", description = "Füge einen Nutzer zu einem Ticket hinzu.", permission = CommandPermission.TICKET_ADD_USER)
public class TicketMemberAddCommand extends TicketCommand {

  private static final String USER_OPTION = "user";
  private final JDA jda;
  private final TicketChannelHelper ticketChannelHelper;

  @Autowired
  public TicketMemberAddCommand(JDA jda, TicketChannelHelper ticketChannelHelper,
      TicketService ticketService) {
    super(ticketService);
    this.jda = jda;
    this.ticketChannelHelper = ticketChannelHelper;
  }

  @Override
  public @Nonnull List<OptionData> getOptions() {
    return List.of(
        new OptionData(
            OptionType.USER,
            USER_OPTION,
            RawMessages.get("interaction.command.ticket.member.add.arg.member"),
            true,
            false
        )
    );
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final User user = getUserOrThrow(interaction, USER_OPTION);

    hook.editOriginal(RawMessages.get("interaction.command.ticket.member.add.adding")).queue();

    if (user.equals(jda.getSelfUser())) {
      throw CommandException.ticketAddBot();
    }

    final Optional<TicketMember> ticketMember = getTicket().getActiveTicketMember(user);

    if (ticketMember.isPresent()) {
      throw CommandException.ticketAddMemberAlreadyInTicket();
    }

    try {
      addTicketMember(user, interaction.getUser(), hook);
    } catch (TicketAddMemberException e) {
      throw CommandException.ticketAddMember(e);
    }
  }

  @Async
  protected void addTicketMember(final User user, final User executor, InteractionHook hook)
      throws CommandException, TicketAddMemberException {
    final TicketMember newTicketMember = TicketMember.createFromTicket(getTicket(), user, executor);

    final TicketMember addedMember = getTicket().addTicketMember(newTicketMember).join();

    if (addedMember == null) {
      throw CommandException.ticketAddMember(new Throwable());
    }

    ticketChannelHelper.addTicketMember(getTicket(), addedMember).join();
    hook.editOriginal(RawMessages.get("interaction.command.ticket.member.add.added")).queue();
    getChannel().sendMessage(user.getAsMention())
        .setEmbeds(getAddedEmbed(executor))
        .queue();
  }

  /**
   * Returns the embed sent when a user is added to a ticket.
   *
   * @param adder the adder
   * @return The embed that is sent when a user is added to a ticket.
   */
  public MessageEmbed getAddedEmbed(@NotNull User adder) {
    return new EmbedBuilder()
        .setTitle(RawMessages.get("interaction.command.ticket.member.embed.title"))
        .setDescription(RawMessages.get("interaction.command.ticket.member.embed.description"))
        .setTimestamp(TimeUtils.berlinTimeProvider().getCurrentTime())
        .setColor(EmbedColors.ADD_TICKET_MEMBER)
        .setFooter(
            RawMessages.get("interaction.command.ticket.member.embed.footer", adder.getName()),
            adder.getAvatarUrl()
        )
        .build();
  }
}
