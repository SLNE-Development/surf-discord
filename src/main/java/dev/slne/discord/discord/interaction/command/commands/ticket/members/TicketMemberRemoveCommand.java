package dev.slne.discord.discord.interaction.command.commands.ticket.members;

import dev.slne.discord.annotation.DiscordCommandMeta;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.command.CommandExceptions;
import dev.slne.discord.exception.ticket.member.TicketRemoveMemberException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.TicketChannelHelper;
import dev.slne.discord.ticket.member.TicketMember;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Ticket member remove command.
 */
@DiscordCommandMeta(name = "remove", description = "Entferne einen Nutzer von einem Ticket.", permission = CommandPermission.TICKET_REMOVE_USER)
public class TicketMemberRemoveCommand extends TicketCommand {

  private static final String USER_OPTION = "user";
  private final JDA jda;
  private final TicketChannelHelper ticketChannelHelper;

  @Autowired
  public TicketMemberRemoveCommand(TicketService ticketService, JDA jda,
      TicketChannelHelper ticketChannelHelper) {
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
            RawMessages.get("interaction.command.ticket.member.remove.arg.member"),
            true,
            false
        )
    );
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final User user = getUserOrThrow(interaction, USER_OPTION);
    checkUserNotBot(user, jda, CommandExceptions.TICKET_BOT_REMOVE);

    final TicketMember ticketMember = getTicket().getActiveTicketMember(user)
        .orElseThrow(CommandExceptions.TICKET_MEMBER_NOT_IN_TICKET::create);

    if (ticketMember.isRemoved()) {
      throw CommandExceptions.TICKET_MEMBER_ALREADY_REMOVED.create();
    }

    final User executor = interaction.getUser();

    try {
      removeTicketMember(user, executor, hook, ticketMember);
    } catch (TicketRemoveMemberException e) {
      throw CommandExceptions.TICKET_REMOVE_MEMBER.create(e);
    }
  }

  @Async
  protected void removeTicketMember(
      @NotNull User user,
      @NotNull User executor,
      @NotNull InteractionHook hook,
      @NotNull TicketMember ticketMember
  ) throws TicketRemoveMemberException {
    hook.editOriginal(RawMessages.get("interaction.command.ticket.member.remove.removing")).queue();
    ticketChannelHelper.removeTicketMember(getTicket(), ticketMember, executor).join();
    hook.editOriginal(RawMessages.get("interaction.command.ticket.member.remove.removed")).queue();

    getChannel()
        .sendMessage(RawMessages.get("interaction.command.ticket.member.remove.announcement",
            user.getAsMention(), executor.getAsMention()))
        .queue();
  }
}
