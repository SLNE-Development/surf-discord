package dev.slne.discord.ticket.message.attachment

import dev.slne.discord.ticket.message.TicketMessage
import jakarta.persistence.*
import net.dv8tion.jda.api.entities.Message.Attachment
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.proxy.HibernateProxy
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "ticket_message_attachments")
data class TicketMessageAttachment(
    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "attachment_id", length = 20)
    val attachmentId: String? = null,

    @Lob
    @Column(name = "filename")
    val filename: String? = null,

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "description", length = 1024)
    val description: String? = null,

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "size")
    val size: Int? = null,

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "content_type")
    val contentType: String? = null,

    @Lob
    @Column(name = "url")
    val url: String? = null,

    @Lob
    @Column(name = "proxy_url")
    val proxyUrl: String? = null,

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "height")
    val height: Int? = null,

    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "width")
    val width: Int? = null,

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "ephemeral")
    val ephemeral: Boolean? = null,

    @JdbcTypeCode(SqlTypes.FLOAT)
    @Column(name = "duration_secs")
    var durationSecs: Float? = null,

    @Lob
    @Column(name = "waveform")
    var waveform: String? = null,
) {

    constructor(attachment: Attachment) : this(
        attachmentId = attachment.id,
        filename = attachment.fileName,
        description = attachment.description,
        size = attachment.size,
        contentType = attachment.contentType,
        url = attachment.url,
        proxyUrl = attachment.proxyUrl,
        height = attachment.height,
        width = attachment.width,
        ephemeral = attachment.isEphemeral,
        durationSecs = attachment.duration.toFloat(),
        waveform = attachment.waveform?.let { Base64.getEncoder().encodeToString(it) }
    )

    @ManyToOne
    @JoinColumn(name = "message_id")
    var ticketMessage: TicketMessage? = null

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as TicketMessageAttachment

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return "TicketMessageAttachment(id=$id, attachmentId=$attachmentId, filename=$filename, description=$description, size=$size, contentType=$contentType, url=$url, proxyUrl=$proxyUrl, height=$height, width=$width, ephemeral=$ephemeral, durationSecs=$durationSecs, waveform=$waveform)"
    }

}

