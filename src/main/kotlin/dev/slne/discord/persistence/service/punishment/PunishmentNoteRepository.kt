package dev.slne.discord.persistence.service.punishment

import dev.slne.discord.persistence.external.PunishmentNote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PunishmentNoteRepository : JpaRepository<PunishmentNote, Long> {
    fun existsPunishmentNoteByNoteId(noteId: UUID): Boolean
}