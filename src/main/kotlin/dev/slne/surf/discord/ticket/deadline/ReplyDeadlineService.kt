package dev.slne.surf.discord.ticket.deadline

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.ticket.database.deadline.DeadlineNotifyRepository
import dev.slne.surf.discord.ticket.database.deadline.ReplyDeadline
import dev.slne.surf.discord.ticket.database.deadline.ReplyDeadlineRepository
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import dev.slne.surf.discord.util.Colors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit

@Service
class ReplyDeadlineService(
    private val jda: JDA,
    private val replyDeadlineRepository: ReplyDeadlineRepository,
    private val deadlineNotifyRepository: DeadlineNotifyRepository,
    private val ticketRepository: TicketRepository,
) {

    suspend fun createDeadline(ticket: Ticket, target: User, setBy: User, deadline: OffsetDateTime) {
        val threadId = ticket.threadId ?: return

        replyDeadlineRepository.create(
            ticketId = ticket.ticketId,
            threadId = threadId,
            targetUserId = target.idLong,
            targetUserName = target.name,
            setById = setBy.idLong,
            setByName = setBy.name,
            deadline = deadline,
        )
    }

    suspend fun onUserReplied(threadId: Long, userId: Long) {
        replyDeadlineRepository.deleteForUserInThread(threadId, userId)
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    protected suspend fun checkExpiredDeadlines() {
        val expired = replyDeadlineRepository.findExpired(OffsetDateTime.now())
        if (expired.isEmpty()) return

        val semaphore = Semaphore(64)

        supervisorScope {
            for (deadline in expired) {
                launch {
                    semaphore.withPermit {
                        handleExpiredDeadline(deadline)
                    }
                }
            }
        }
    }

    private suspend fun handleExpiredDeadline(deadline: ReplyDeadline) {
        val deleted = replyDeadlineRepository.delete(deadline.id)
        if (!deleted) return

        val ticket = ticketRepository.getTicketById(deadline.ticketId)
        if (ticket == null || ticket.isClosed()) return

        try {
            if (deadlineNotifyRepository.isEnabled(deadline.setById)) {
                notifyDeadlineCreator(deadline)
            }
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception

            logger.warn(
                "Failed to send reply-deadline notification for deadline {} to user {}",
                deadline.id,
                deadline.setById,
                exception
            )
        }
    }

    private suspend fun notifyDeadlineCreator(deadline: ReplyDeadline) {
        try {
            val privateChannel = jda.openPrivateChannelById(deadline.setById).await()
            privateChannel.sendMessageEmbeds(embed {
                title = translatable("ticket.reply-deadline.notify.title")
                description = translatable(
                    "ticket.reply-deadline.notify.description",
                    "<@${deadline.targetUserId}>",
                    "<#${deadline.threadId}>"
                )
                color = Colors.WARNING
            }).await()
        } catch (e: ErrorResponseException) {
            when (e.errorCode) {
                50007, 50278 -> {
                    logger.info(
                        "Cannot DM reply-deadline notification to user {} for deadline {}. " +
                                "The user likely disabled server DMs, blocked the bot, or Discord rejected the DM. code={}",
                        deadline.setById,
                        deadline.id,
                        e.errorCode
                    )
                    return
                }

                else -> throw e
            }
        }
    }
}