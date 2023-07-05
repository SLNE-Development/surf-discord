package dev.slne.discord.discord.interaction.command.commands.ticket.members;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.commands.TicketCommand;
import dev.slne.discord.ticket.TicketChannel;
import dev.slne.discord.ticket.member.TicketMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class TicketMemberRemoveCommand extends TicketCommand {

    /**
     * Creates a new TicketMemberRemoveCommand.
     */
    public TicketMemberRemoveCommand() {
        super("remove", "Entferne einen Nutzer von einem Ticket.");
    }

    @Override
    public @Nonnull List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();

        options.add(new OptionData(OptionType.USER, "user", "Der Nutzer, der hinzugefügt werden soll.", true, false));

        return options;
    }

    @Override
    public @Nonnull DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_TICKET_REMOVE_USER;
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        interaction.deferReply(true).queue(hook -> {
            OptionMapping userOption = interaction.getOption("user");

            if (userOption == null) {
                hook.editOriginal("Du musst einen Nutzer angeben.").queue();
                return;
            }

            User user = userOption.getAsUser();

            if (user.equals(DiscordBot.getInstance().getJda().getSelfUser())) {
                hook.editOriginal("Du kannst den Bot nicht entfernen.").queue();
                return;
            }

            TicketMember ticketMember = getTicket().getActiveTicketMember(user);

            if (ticketMember == null) {
                hook.editOriginal("Dieser Nutzer ist nicht in diesem Ticket.").queue();
                return;
            }

            if (ticketMember.isRemoved()) {
                hook.editOriginal("Dieser Nutzer wurde bereits entfernt.").queue();
                return;
            }

            ticketMember.setRemovedByAvatarUrl(interaction.getUser().getAvatarUrl());
            ticketMember.setRemovedById(interaction.getUser().getId());
            ticketMember.setRemovedByName(interaction.getUser().getName());
            getTicket().removeTicketMember(ticketMember).whenComplete(ticketMemberRemoved -> {
                if (ticketMemberRemoved == null) {
                    hook.editOriginal("Der Nutzer konnte nicht entfernt werden.").queue();
                    return;
                }

                TicketChannel.removeTicketMember(getTicket(), ticketMember).whenComplete(v -> {
                    hook.editOriginal("Der Nutzer wurde entfernt.").queue();
                    getChannel().sendMessage(
                            user.getAsMention() + " wurde von " + interaction.getUser().getAsMention() + " entfernt.")
                            .queue();
                }, failure -> Launcher.getLogger()
                        .logError("Error while updating channel permissions: " + failure.getMessage()));
            });
        });
    }

}
