package dev.slne.surf.discord.announcement

import dev.slne.surf.discord.announcement.database.AnnouncementRepository
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.util.Colors
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import org.springframework.stereotype.Service

@Service
class AnnouncementService(
    private val announcementRepository: AnnouncementRepository
) {
    suspend fun sendAnnouncement(
        author: User,
        title: String,
        content: String,
        channel: MessageChannel
    ) {
        val message = channel.sendMessageEmbeds(embed {
            this.title = title
            this.description = content
            this.color = Colors.INFO
        }).submit(true).await()
        val messageId = message.idLong
        val channelId = message.channelIdLong

        announcementRepository.createAnnouncement(
            author.name,
            author.idLong,
            messageId,
            channelId,
            title,
            content
        )
    }

    suspend fun getAnnouncement(messageId: Long) =
        announcementRepository.getAnnouncement(messageId)

    suspend fun isAnnouncement(messageId: Long) =
        announcementRepository.getAnnouncement(messageId) != null

    suspend fun editAnnouncement(
        announcement: DiscordAnnouncement,
        newTitle: String,
        newContent: String
    ) {
        val channel =
            jda.getGuildChannelById(announcement.channelId) as? GuildMessageChannel ?: return
        channel.editMessageEmbedsById(
            announcement.messageId,
            embed {
                title = newTitle
                description = newContent
                color = Colors.INFO
            }
        ).queue()

        announcementRepository.editAnnouncement(
            announcement.messageId,
            newTitle,
            newContent
        )
    }

    suspend fun deleteAnnouncement(announcement: DiscordAnnouncement) {
        val channel =
            jda.getGuildChannelById(announcement.channelId) as? GuildMessageChannel ?: return

        channel.deleteMessageById(announcement.messageId).queue()
        announcementRepository.deleteAnnouncement(announcement.messageId)
    }
}