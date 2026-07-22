package dev.slne.surf.discord.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.logging.TicketLogger
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.getRolesWithPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataRepository
import dev.slne.surf.discord.ticket.database.ticket.staff.TicketStaffRepository
import dev.slne.surf.discord.util.Colors
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.interactions.InteractionHook
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val ticketDataRepository: TicketDataRepository,
    private val ticketStaffRepository: TicketStaffRepository,
    private val ticketMemberService: TicketMemberService,
    private val ticketChannel: TextChannel?,
    private val ticketLogger: TicketLogger
) {
    private val closeThumbnail = Thumbnail.fromUrl("https://cdn3.emoji.gg/emojis/4569-ok.png")

    suspend fun createTicket(hook: InteractionHook, type: TicketType, data: TicketData): Ticket? {
        val userId = hook.interaction.user.idLong
        val user = hook.interaction.user

        if (hasOpenTicket(userId, type)) {
            return null
        }

        val threadChannel = runCatching {
            ticketChannel
                ?.createThreadChannel("${type.id}-${hook.interaction.user.name}", true)
                ?.setInvitable(false)
                ?.await()
        }.getOrNull() ?: run {
            hook.editOriginal(translatable("error")).queue()
            return null
        }

        if (type != TicketType.APPLICATION) {
            val roles = type.viewPermission.getRolesWithPermission(threadChannel.guild.idLong)
            addRoles(threadChannel, roles)
        } else {
            val applicationType = TicketApplicationType.valueOf(
                data["application_type"] ?: error("Missing application type")
            )

            val viewingRoles =
                applicationType.viewPermission.getRolesWithPermission(threadChannel.guild.idLong)
            addRoles(threadChannel, viewingRoles)
        }

        threadChannel.addThreadMember(user).queue()

        val ticket = Ticket(
            ticketId = UUID.randomUUID(),
            ticketData = mapOf(),
            authorId = userId,
            authorName = user.name,
            authorAvatar = user.avatarUrl,
            guildId = threadChannel.guild.idLong,
            threadId = threadChannel.idLong,
            ticketType = type,
            createdAt = ZonedDateTime.now(),
            closedAt = null,
            closedById = null,
            closedByName = null,
            closedByAvatar = null,
            closedReason = null
        )

        ticketRepository.createTicket(ticket)
        ticketRepository.getInternalId(ticket.ticketId)?.let {
            ticketDataRepository.setData(it, data)
        }

        ticketMemberService.addMember(ticket, user, jda.selfUser, false)
        ticketLogger.logCreation(ticket)

        return ticket
    }

    private suspend fun addRoles(threadChannel: ThreadChannel, roles: Collection<Long>) =
        supervisorScope {
            for (roleId in roles) {
                launch {
                    val message = threadChannel
                        .sendMessage("Granting access for $roleId...")
                        .await()

                    message
                        .editMessage("<@&$roleId>")
                        .await()

                    message.delete().await()
                }
            }
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
        ticketRepository.getInternalId(ticket.ticketId)?.let {
            ticketDataRepository.setData(it, ticketData)
        }


    suspend fun getTicketByThreadId(threadId: Long) =
        ticketRepository.getTicketByThreadId(threadId)

    suspend fun isTicketExisting(threadId: Long) =
        ticketRepository.getTicketByThreadId(threadId) != null

    suspend fun hasOpenTicket(userId: Long, ticketType: TicketType) =
        ticketRepository.hasOpenTicket(userId, ticketType)

    suspend fun getTicketByUserAndType(userId: Long, ticketType: TicketType) =
        ticketRepository.getTicket(userId, ticketType)

    suspend fun closeTicket(reason: String, hook: InteractionHook) {
        val ticket =
            getTicketByThreadId(hook.interaction.channel?.idLong ?: 0L) ?: error("Not a ticket")
        val thread = ticket.getThreadChannel() ?: error("Not a ticket")
        val closer = hook.interaction.user
        val closerMember = hook.interaction.member ?: error("Member is null")

        if (ticketStaffRepository.isClaimed(ticket)) {
            if (!ticketStaffRepository.isClaimedByUser(
                    ticket,
                    closer
                ) && !closerMember.hasPermission(DiscordPermission.TICKET_CLOSE_BYPASS_CLAIM)
            ) {
                hook.editOriginalEmbeds(embed {
                    title = translatable("ticket.close.denied.title")
                    description = translatable("ticket.close.claimed-by-other")
                    color = Colors.ERROR
                }).queue()
                return
            }
        }

        thread.sendMessageComponents(
            Container.of(
                TextDisplay.of("## Ticket Geschlossen"),
                TextDisplay.of("Das Ticket wurde von ${closer.asMention} geschlossen. \n \n**Grund**: $reason"),
                Separator.createDivider(Separator.Spacing.LARGE),
                TextDisplay.of("**Ticket Typ**: \n${ticket.ticketType.displayName}"),
                TextDisplay.of("**Ticket Id**: \n${ticket.ticketId}"),
                TextDisplay.of("**Ticket Author**: \n<@${ticket.authorId}>"),
                TextDisplay.of(
                    "**Ticket Erstellungsdatum**: \n<t:${
                        ticket.createdAt.toEpochSecond()
                    }:F>"
                ),
                TextDisplay.of("**Ticket Schließungsdatum**: \n<t:${System.currentTimeMillis() / 1000}:F>"),
            ),
            Separator.createDivider(Separator.Spacing.LARGE),
        ).useComponentsV2().setAllowedMentions(listOf()).queue()

        ticket.closedAt = ZonedDateTime.now()
        ticket.closedById = closer.idLong
        ticket.closedByName = closer.name
        ticket.closedByAvatar = closer.avatarUrl
        ticket.closedReason = reason

        ticketLogger.logClosure(ticket)
        markAsClosed(ticket)

        logger.info("Ticket ${ticket.ticketId} closed by ${closer.name} (type=${ticket.ticketType}, creator=${ticket.authorName})")

        jda.openPrivateChannelById(ticket.authorId).submit(true).thenAccept {
            it.sendMessageEmbeds(embed {
                title = "Dein Ticket wurde geschlossen"
                description =
                    "Dein ${ticket.ticketType.displayName} wurde geschlossen."
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
                    value = "<t:${ticket.createdAt.toInstant().toEpochMilli() / 1000}:F>"
                    inline = true
                }

                field {
                    name = "Schließungsdatum"
                    value = "<t:${ZonedDateTime.now().toInstant().toEpochMilli() / 1000}:F>"
                    inline = true
                }
            }).queue(null) {
                // User has Pms disabled, ignore
            }
        }

        thread.manager.setLocked(true).await()
        thread.manager.setArchived(true).await()
    }

    suspend fun markAsClosed(ticket: Ticket) =
        ticketRepository.markAsClosed(ticket)
}