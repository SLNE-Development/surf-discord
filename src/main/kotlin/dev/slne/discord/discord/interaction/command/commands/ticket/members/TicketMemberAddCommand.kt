package dev.slne.discord.discord.interaction.command.commands.ticket.members

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.checkUserNotBot
import dev.slne.discord.discord.interaction.command.commands.TicketCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.RawMessages
import dev.slne.discord.message.toEuropeBerlin
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.time.ZonedDateTime


private const val USER_OPTION = "user"

@DiscordCommandMeta(
    name = "add",
    description = "Füge einen Nutzer zu einem Ticket hinzu.",
    permission = CommandPermission.TICKET_ADD_USER
)
class TicketMemberAddCommand : TicketCommand() {
    override val options = listOf(
        OptionData(
            OptionType.USER,
            USER_OPTION,
            RawMessages.get("interaction.command.ticket.member.add.arg.member"),
            true,
            false
        )
    )


    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val user = interaction.getUserOrThrow(USER_OPTION)
        user.checkUserNotBot(CommandExceptions.TICKET_BOT_ADD)

        hook.editOriginal(RawMessages.get("interaction.command.ticket.member.add.adding")).await()

        val thread = interaction.getThreadChannelOrThrow()
        val ticketMembers = thread.retrieveThreadMembers().await()
        val userMember = thread.guild.retrieveMember(user).await()

        if (ticketMembers.any { it.id == userMember.id }) {
            throw CommandExceptions.TICKET_MEMBER_ALREADY_IN_TICKET.create()
        }

        addTicketMember(userMember, interaction.user, thread, hook)
    }

    private suspend fun addTicketMember(
        member: Member,
        executor: User,
        threadChannel: ThreadChannel,
        hook: InteractionHook
    ) {
        threadChannel.addThreadMember(member).await()

        hook.editOriginal(RawMessages.get("interaction.command.ticket.member.add.added")).await()

        hook.sendMessage(MessageCreate {
            content = member.asMention
            embed {
                title = RawMessages.get("interaction.command.ticket.member.embed.title")
                description = RawMessages.get("interaction.command.ticket.member.embed.description")
                timestamp = ZonedDateTime.now().toEuropeBerlin()
                color = EmbedColors.ADD_TICKET_MEMBER
                footer {
                    name = RawMessages.get(
                        "interaction.command.ticket.member.embed.footer",
                        executor.name
                    )
                    iconUrl = executor.getAvatarUrl()
                }
            }
        }).await()
    }
}
