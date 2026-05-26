package dev.slne.surf.discord.ticket.deadline

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.ticket.database.deadline.DeadlineNotifyRepository
import dev.slne.surf.discord.ticket.database.deadline.ReplyDeadline
import dev.slne.surf.discord.ticket.database.deadline.ReplyDeadlineRepository
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@Service
class ReplyDeadlineService(
    private val jda: JDA,
    private val replyDeadlineRepository: ReplyDeadlineRepository,
    private val deadlineNotifyRepository: DeadlineNotifyRepository,
) {

    suspend fun createDeadline(ticket: Ticket, target: User, setBy: User, deadline: ZonedDateTime) {
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

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    protected suspend fun checkExpiredDeadlines() {
        val expired = replyDeadlineRepository.findExpired(ZonedDateTime.now())

        for (deadline in expired) {
            try {
                if (deadlineNotifyRepository.isEnabled(deadline.setById)) {
                    notify(deadline)
                }
            } catch (exception: Exception) {
                logger.warn(
                    "Failed to send reply-deadline notification for deadline {} to user {}",
                    deadline.id,
                    deadline.setById,
                    exception
                )
            } finally {
                replyDeadlineRepository.delete(deadline.id)
            }
        }
    }

    private suspend fun notify(deadline: ReplyDeadline) {
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
    }
}