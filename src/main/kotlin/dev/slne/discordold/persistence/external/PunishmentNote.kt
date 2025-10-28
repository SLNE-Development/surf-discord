package dev.slne.discordold.persistence.external

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Table(name = "punish_notes")
@Entity
data class PunishmentNote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "note_id", nullable = false, length = 36, unique = true)
    @JdbcTypeCode(SqlTypes.CHAR)
    val noteId: UUID,

    @Column(name = "notable_type", nullable = false, length = 255)
    val notableType: String,

    @Column(name = "notable_id", nullable = false)
    val notableId: Long,

    @Lob
    @Column(name = "note", nullable = false)
    val note: String,

    @Column(name = "creator_id", nullable = true)
    val creatorId: Long? = null,

    @Column(name = "generated", nullable = false)
    val generated: Boolean = true,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PunishmentNote

        if (id != other.id) return false
        if (notableId != other.notableId) return false
        if (creatorId != other.creatorId) return false
        if (generated != other.generated) return false
        if (noteId != other.noteId) return false
        if (notableType != other.notableType) return false
        if (note != other.note) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + notableId.hashCode()
        result = 31 * result + (creatorId?.hashCode() ?: 0)
        result = 31 * result + generated.hashCode()
        result = 31 * result + noteId.hashCode()
        result = 31 * result + notableType.hashCode()
        result = 31 * result + note.hashCode()
        return result
    }

    override fun toString(): String {
        return "PunishmentNote(id=$id, noteId=$noteId, notableType='$notableType', notableId=$notableId, note='$note', creatorId=$creatorId, generated=$generated)"
    }

}
