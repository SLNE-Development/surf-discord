package dev.slne.discord.ticket.message.attachment

import dev.slne.discord.ticket.message.TicketMessage
import jakarta.persistence.*
import net.dv8tion.jda.api.entities.Message
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "ticket_message_attachments")
open class TicketMessageAttachment protected constructor() {

    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    open var id: Long? = null
        protected set

    @Column(name = "attachment_id", nullable = false, length = 20)
    open var attachmentId: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "filename", nullable = false)
    open var filename: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "title")
    open var title: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "description", length = 1024)
    open var description: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "size", nullable = false)
    open var size: Int? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "content_type")
    open var contentType: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "url", nullable = false)
    open var url: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "proxy_url")
    open var proxyUrl: String? = null

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "height", nullable = false)
    open var height: Int? = null
        protected set

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "width", nullable = false)
    open var width: Int? = null
        protected set

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "ephemeral", nullable = false)
    open var ephemeral: Boolean? = null
        protected set

    @JdbcTypeCode(SqlTypes.FLOAT)
    @Column(name = "duration_secs", nullable = false)
    open var durationSecs: Float? = null
        protected set

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Lob
    @Column(name = "waveform")
    open var waveform: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "flags", nullable = false)
    open var flags: Int? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "message_id")
    open var ticketMessage: TicketMessage? = null

    companion object {
        fun fromJda(attachment: Message.Attachment) = TicketMessageAttachment().apply {
            attachmentId = attachment.id
            filename = attachment.fileName
//            title = attachment. TODO
            description = attachment.description
            size = attachment.size
            contentType = attachment.contentType
            url = attachment.url
            proxyUrl = attachment.proxyUrl
            height = attachment.height
            width = attachment.width
            ephemeral = attachment.isEphemeral

            if (attachment.duration != 0.0) {
                durationSecs = attachment.duration.toFloat()
            }

            waveform = attachment.waveform?.let { Base64.getEncoder().encodeToString(it) }
//            flags = attachment.fla TODO
        }
    }
}

fun Message.Attachment.toTicketMessageAttachment() = TicketMessageAttachment.fromJda(this)
