package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.logging.TicketLogger
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.getRolesWithPermission
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataRepository
import dev.slne.surf.discord.ticket.database.ticket.staff.TicketStaffRepository
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.random
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.InteractionHook
import org.springframework.stereotype.Service

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val ticketDataRepository: TicketDataRepository,
    private val ticketStaffRepository: TicketStaffRepository,
    private val ticketChannel: TextChannel?,
    private val ticketLogger: TicketLogger
) {
    suspend fun createTicket(hook: InteractionHook, type: TicketType, data: TicketData): Ticket? {
        val userId = hook.interaction.user.idLong
        val user = hook.interaction.user

        if (hasOpenTicket(userId, type)) {
            return null
        }

        val threadChannel = ticketChannel
            ?.createThreadChannel("${type.id}-${hook.interaction.user.name}", true)
            ?.setInvitable(false)
            ?.complete(true) ?: run {
            hook.editOriginal(translatable("error")).queue()
            return null
        }

        type.viewPermission.getRolesWithPermission(threadChannel.guild.idLong).forEach {
            val message = threadChannel.sendMessage("Zugriff für $it").submit(true).await()
            message.editMessage("<@&$it>").queue()
            message.delete().queue()
        }

        threadChannel.addThreadMember(user).queue()

        val ticket = Ticket(
            random.nextLong(),
            ObjectArraySet(),
            userId,
            user.name,
            user.avatarUrl,
            threadChannel.guild.idLong,
            threadChannel.idLong,
            type,
            System.currentTimeMillis(),
            null,
            null,
            null,
            null,
            null
        )

        ticketRepository.createTicket(ticket)
        ticketDataRepository.setData(ticket.ticketId, data)

        ticketLogger.logCreation(ticket)

        return ticket
    }

    suspend fun claim(ticket: Ticket, user: User) {
        ticketStaffRepository.claim(ticket, user)

        ticketLogger.logNewClaimant(ticket, user.name)

        ticket.getThreadChannel()?.sendMessageEmbeds(embed {
            title = translatable("ticket.claimed.title")
            description = translatable("ticket.claimed.description", user.asMention)
            color = Colors.INFO
        })?.queue()
    }

    suspend fun unclaim(ticket: Ticket, user: User) {
        ticketStaffRepository.unclaim(ticket)
        ticketLogger.logNewUnClaimant(ticket, user.name)
    }

    suspend fun isClaimed(ticket: Ticket) =
        ticketStaffRepository.isClaimed(ticket)

    suspend fun isClaimedByUser(ticket: Ticket, user: User) =
        ticketStaffRepository.isClaimedByUser(ticket, user)

    suspend fun updateData(ticket: Ticket, ticketData: TicketData) =
        ticketDataRepository.setData(ticket.ticketId, ticketData)

    suspend fun getTicketByThreadId(threadId: Long) =
        ticketRepository.getTicketByThreadId(threadId)

    suspend fun hasOpenTicket(userId: Long, ticketType: TicketType) =
        ticketRepository.hasOpenTicket(userId, ticketType)

    suspend fun getTicketByUserAndType(userId: Long, ticketType: TicketType) =
        ticketRepository.getTicket(userId, ticketType)

    suspend fun closeTicket(ticket: Ticket, reason: String, closer: User) {
        val thread = ticket.getThreadChannel() ?: return

        thread.sendMessageEmbeds(
            embed {
                title = "Ticket Geschlossen"
                description =
                    "Das Ticket wurde von ${closer.asMention} geschlossen. \n \nGrund: $reason"
                color = Colors.ERROR

                field {
                    name = "Ticket Typ"
                    value = ticket.ticketType.displayName
                    inline = true
                }

                field {
                    name = "Ticket Id"
                    value = ticket.ticketId.toString()
                    inline = true
                }

                field {
                    name = "Ticket Author"
                    value = "<@${ticket.authorId}>"
                    inline = true

                }

                field {
                    name = "Ticket Erstellungsdatum"
                    value = "<t:${ticket.createdAt / 1000}:F>"
                    inline = true
                }

                field {
                    name = "Ticket Schließungsdatum"
                    value = "<t:${System.currentTimeMillis() / 1000}:F>"
                    inline = true
                }
            }
        ).queue()

        ticket.closedAt = System.currentTimeMillis()
        ticket.closedById = closer.idLong
        ticket.closedByName = closer.name
        ticket.closedByAvatar = closer.avatarUrl
        ticket.closedReason = reason

        ticketLogger.logClosure(ticket)

        jda.openPrivateChannelById(ticket.authorId).submit(true).thenAccept {
            it.sendMessageEmbeds(embed {
                title = "Dein Ticket wurde geschlossen"
                description =
                    "Dein ${ticket.ticketType.displayName} wurde geschlossen.\n\nGrund: $reason"
                color = Colors.INFO

                field {
                    name = "Ticket Typ"
                    value = ticket.ticketType.displayName
                    inline = true
                }

                field {
                    name = "Ticket Id"
                    value = ticket.ticketId.toString()
                    inline = true
                }

                field {
                    name = "Geschlossen von"
                    value = closer.asMention
                    inline = true
                }

                field {
                    name = "Schließungsgrund"
                    value = reason
                    inline = true
                }

                field {
                    name = "Erstellungsdatum"
                    value = "<t:${ticket.createdAt / 1000}:F>"
                    inline = true
                }

                field {
                    name = "Schließungsdatum"
                    value = "<t:${System.currentTimeMillis() / 1000}:F>"
                    inline = true
                }
            }).queue()
        }

        markAsClosed(ticket)
        thread.manager.setLocked(true).queue()
        thread.manager.setArchived(true).queue()
    }

    suspend fun closeTicket(hook: InteractionHook, reason: String) {
        val ticket = getTicketByThreadId(hook.interaction.channel?.idLong ?: 0L) ?: return

        closeTicket(ticket, reason, hook.interaction.user)
    }

    suspend fun markAsClosed(
        ticket: Ticket
    ) =
        ticketRepository.markAsClosed(
            ticket
        )
}