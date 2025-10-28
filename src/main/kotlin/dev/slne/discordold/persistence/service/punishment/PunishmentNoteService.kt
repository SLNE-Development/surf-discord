package dev.slne.discordold.persistence.service.punishment

import dev.slne.discordold.persistence.external.PunishmentNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.util.*

@Service
class PunishmentNoteService(private val punishmentNoteRepository: PunishmentNoteRepository) {

    suspend fun createPunishmentNote(punishmentNote: PunishmentNote) = withContext(Dispatchers.IO) {
        punishmentNoteRepository.save(punishmentNote)
    }

    suspend fun existsPunishmentNoteByNoteId(noteId: UUID) = withContext(Dispatchers.IO) {
        punishmentNoteRepository.existsPunishmentNoteByNoteId(noteId)
    }

    suspend fun generateNoteId(): UUID = withContext(Dispatchers.IO) {
        var noteId: UUID
        
        do {
            noteId = UUID.randomUUID()
        } while (existsPunishmentNoteByNoteId(noteId))

        noteId
    }

}