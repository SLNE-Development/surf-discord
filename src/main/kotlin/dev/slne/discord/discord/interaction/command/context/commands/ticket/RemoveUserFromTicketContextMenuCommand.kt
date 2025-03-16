package dev.slne.discord.discord.interaction.command.context.commands.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordContextMenuCommandMeta
import dev.slne.discord.discord.interaction.command.context.DiscordContextMenuCommand
import dev.slne.discord.discord.interaction.command.context.DiscordContextMenuCommandType
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

@DiscordContextMenuCommandMeta(
    name = "Remove user from Ticket",
    type = DiscordContextMenuCommandType.USER,
    permission = CommandPermission.TICKET_REMOVE_USER,
)
class RemoveUserFromTicketContextMenuCommand : DiscordContextMenuCommand<User>() {

    override suspend fun internalExecute(
        interaction: GenericContextInteractionEvent<User>,
        hook: InteractionHook
    ) {
        val channel = interaction.channel as? ThreadChannel ?: run {
            hook.editOriginal(translatable("interaction.context.menu.ticket-admin-panel.not-a-thread"))
                .await()
            return
        }

        val member = channel.guild.retrieveMember(interaction.target).await()
        val ticketMembers = channel.retrieveThreadMembers().await()

        if (ticketMembers.none { it.id == member.id }) {
            throw CommandExceptions.TICKET_MEMBER_NOT_IN_TICKET.create()
        }

        hook.editOriginal(translatable("interaction.command.ticket.member.remove.removing")).await()

        val removedMessage = channel.sendMessage(
            translatable(
                "interaction.command.ticket.member.remove.announcement",
                member.asMention,
                interaction.user.asMention
            )
        ).await()

        try {
            channel.removeThreadMember(member).await()
            hook.editOriginal(translatable("interaction.command.ticket.member.remove.removed"))
                .await()
        } catch (e: Exception) {
            removedMessage.editMessage(translatable("interaction.command.ticket.member.remove.error"))
                .await()
        }
    }
}
