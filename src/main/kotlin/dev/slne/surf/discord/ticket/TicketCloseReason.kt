package dev.slne.surf.discord.ticket

data class TicketCloseReason(
    val displayName: String,
    val description: String
) {
    companion object {
        fun of(displayName: String, description: String) = TicketCloseReason(
            displayName = displayName,
            description = description
        )
    }
}