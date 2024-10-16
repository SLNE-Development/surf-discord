package dev.slne.discord.discord.interaction.command.commands.ticket.members

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.checkMemberNotBot
import dev.slne.discord.discord.interaction.command.commands.TicketCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import java.time.ZonedDateTime


private const val USER_OPTION = "user"

@DiscordCommandMeta(
    name = "add",
    description = "Füge einen Nutzer zu einem Ticket hinzu.",
    permission = CommandPermission.TICKET_ADD_USER
)
object TicketMemberAddCommand : TicketCommand() {
    override val options = listOf(
        option<Member>(
            USER_OPTION,
            translatable("interaction.command.ticket.member.add.arg.member"),
        )
    )


    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val member = interaction.getOptionOrThrow<Member>(USER_OPTION)
        member.checkMemberNotBot(CommandExceptions.TICKET_BOT_ADD)

        hook.editOriginal(translatable("interaction.command.ticket.member.add.adding")).await()

        val thread = interaction.getThreadChannelOrThrow()
        val ticketMembers = thread.retrieveThreadMembers().await()

        if (ticketMembers.any { it.id == member.id }) {
            throw CommandExceptions.TICKET_MEMBER_ALREADY_IN_TICKET.create()
        }

        addTicketMember(member, interaction.user, thread, hook)
    }

    private suspend fun addTicketMember(
        member: Member,
        executor: User,
        threadChannel: ThreadChannel,
        hook: InteractionHook
    ) {
        threadChannel.addThreadMember(member).await()

        hook.editOriginal(translatable("interaction.command.ticket.member.add.added")).await()

        hook.sendMessage(MessageCreate {
            content = member.asMention
            embed {
                title = translatable("interaction.command.ticket.member.embed.title")
                description = translatable("interaction.command.ticket.member.embed.description")
                timestamp = ZonedDateTime.now()
                color = EmbedColors.ADD_TICKET_MEMBER
                footer {
                    name = translatable(
                        "interaction.command.ticket.member.embed.footer",
                        executor.name
                    )
                    iconUrl = executor.getAvatarUrl()
                }
            }
        }).await()
    }
}
