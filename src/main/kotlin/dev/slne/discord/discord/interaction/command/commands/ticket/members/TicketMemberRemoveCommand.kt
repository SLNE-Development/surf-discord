package dev.slne.discord.discord.interaction.command.commands.ticket.members

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.checkUserNotBot
import dev.slne.discord.discord.interaction.command.commands.TicketCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.RawMessages
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData


private const val USER_OPTION = "user"

@DiscordCommandMeta(
    name = "remove",
    description = "Entferne einen Nutzer aus einem Ticket.",
    permission = CommandPermission.TICKET_REMOVE_USER
)
class TicketMemberRemoveCommand : TicketCommand() {

    override val options = listOf(
        OptionData(
            OptionType.USER,
            USER_OPTION,
            RawMessages.get("interaction.command.ticket.member.remove.arg.member"),
            true,
            false
        )
    )


    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val user = interaction.getUserOrThrow(USER_OPTION)
        user.checkUserNotBot(CommandExceptions.TICKET_BOT_REMOVE)

        val thread = interaction.getThreadChannelOrThrow()
        val ticketMembers = thread.retrieveThreadMembers().await()
        val userMember = thread.guild.retrieveMember(user).await()

        if (ticketMembers.none { it.id == userMember.id }) {
            throw CommandExceptions.TICKET_MEMBER_NOT_IN_TICKET.create()
        }

        val executor = interaction.user
        removeTicketMember(userMember, executor, thread, hook)
    }

    private suspend fun removeTicketMember(
        member: Member,
        executor: User,
        thread: ThreadChannel,
        hook: InteractionHook,
    ) {
        hook.editOriginal(RawMessages.get("interaction.command.ticket.member.remove.removing"))
            .await()

        thread.removeThreadMember(member).await()

        hook.editOriginal(RawMessages.get("interaction.command.ticket.member.remove.removed"))
            .await()

        thread.sendMessage(
            RawMessages.get(
                "interaction.command.ticket.member.remove.announcement",
                member.asMention, executor.asMention
            )
        ).await()
    }
}
