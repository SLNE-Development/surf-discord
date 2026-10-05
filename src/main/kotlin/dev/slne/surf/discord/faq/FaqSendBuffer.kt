package dev.slne.surf.discord.faq

import java.time.OffsetDateTime

data class FaqSender(
    val id: String,
    val name: String
)

data class FaqSend(
    val faqId: ULong,
    val platform: FaqPlatform,
    val sender: FaqSender,
    val sentAt: OffsetDateTime
)

class FaqSendBuffer {
    private val pending = mutableListOf<FaqSend>()

    @Synchronized
    fun record(send: FaqSend) {
        pending += send
    }

    @Synchronized
    fun drain(): List<FaqSend> {
        val drained = pending.toList()
        pending.clear()
        return drained
    }

    @Synchronized
    fun restore(sends: List<FaqSend>) {
        pending.addAll(0, sends)
    }
}
