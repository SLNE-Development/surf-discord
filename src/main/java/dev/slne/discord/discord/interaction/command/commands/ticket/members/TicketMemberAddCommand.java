package dev.slne.discord.discord.interaction.command.commands.ticket.members;

import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.exception.command.CommandException;
import dev.slne.discord.exception.ticket.member.TicketAddMemberException;
import dev.slne.discord.guild.permission.CommandPermission;
import dev.slne.discord.spring.annotation.DiscordCommandMeta;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.TicketChannelHelper;
import dev.slne.discord.ticket.member.TicketMember;
import java.awt.Color;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
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
        new OptionData(OptionType.USER, USER_OPTION, "Der Nutzer, welcher hinzugefügt werden soll.",
            true,
            false)
    );
  }

  @Override
  public void internalExecute(SlashCommandInteractionEvent interaction, InteractionHook hook)
      throws CommandException {
    final OptionMapping userOption = interaction.getOption(USER_OPTION);

    if (userOption == null) {
      throw new CommandException("Du musst einen Nutzer angeben.");
    }

    final User user = userOption.getAsUser();

    if (user.equals(jda.getSelfUser())) {
      throw new CommandException("Du kannst den Bot nicht hinzufügen.");
    }

    final TicketMember ticketMember = getTicket().getActiveTicketMember(user);

    if (ticketMember != null) {
      throw new CommandException("Dieser Nutzer ist bereits in diesem Ticket.");
    }

    try {
      addTicketMember(user, interaction.getUser(), hook);
    } catch (TicketAddMemberException e) {
      throw new CommandException("Der Nutzer konnte nicht hinzugefügt werden.", e);
    }
  }

  @Async
  protected void addTicketMember(final User user, final User executor, InteractionHook hook)
      throws CommandException, TicketAddMemberException {
    final TicketMember newTicketMember = TicketMember.createFromTicket(getTicket(), user, executor);

    final TicketMember addedMember = getTicket().addTicketMember(newTicketMember).join();

    if (addedMember == null) {
      throw new CommandException("Der Nutzer konnte nicht hinzugefügt werden.");
    }

    ticketChannelHelper.addTicketMember(getTicket(), addedMember).join();
    hook.editOriginal("Der Nutzer wurde erfolgreich hinzugefügt.").queue();
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
        .setTitle("Willkommen im Ticket!")
        .setDescription(
            "Du wurdest zu einem Ticket hinzugefügt. Bitte sieh dir den Verlauf des Tickets an und warte auf eine Nachricht eines Teammitglieds."
        )
        .setTimestamp(Instant.now())
        .setColor(Color.WHITE)
        .setFooter("Hinzugefügt von " + adder.getName(), adder.getAvatarUrl())
        .build();
  }
}
