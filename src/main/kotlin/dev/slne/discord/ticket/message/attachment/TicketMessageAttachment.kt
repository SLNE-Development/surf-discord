package dev.slne.discord.ticket.message.attachment

import dev.slne.discord.ticket.message.TicketMessage
import jakarta.persistence.*
import net.dv8tion.jda.api.entities.Message
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "ticket_test_message_attachments")
open class TicketMessageAttachment protected constructor() {

    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @Column(name = "attachment_id", length = 20)
    open var attachmentId: String? = null
        protected set

    @Lob
    @Column(name = "filename")
    open var filename: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "description", length = 1024)
    open var description: String? = null
        protected set

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "size")
    open var size: Int? = null
        protected set

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "content_type")
    open var contentType: String? = null
        protected set

    @Lob
    @Column(name = "url")
    open var url: String? = null
        protected set

    @Lob
    @Column(name = "proxy_url")
    open var proxyUrl: String? = null

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "height")
    open var height: Int? = null
        protected set

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "width")
    open var width: Int? = null
        protected set

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "ephemeral")
    open var ephemeral: Boolean? = null
        protected set

    @JdbcTypeCode(SqlTypes.FLOAT)
    @Column(name = "duration_secs")
    open var durationSecs: Float? = null
        protected set

    @Lob
    @Column(name = "waveform")
    open var waveform: String? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "message_id")
    open var ticketMessage: TicketMessage? = null

    companion object {
        fun fromJda(attachment: Message.Attachment) = TicketMessageAttachment().apply {
            attachmentId = attachment.id
            filename = attachment.fileName
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
        }
    }
}

fun Message.Attachment.toTicketMessageAttachment() = TicketMessageAttachment.fromJda(this)
