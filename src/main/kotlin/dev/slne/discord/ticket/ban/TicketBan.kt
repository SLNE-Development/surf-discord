package dev.slne.discord.ticket.ban

import jakarta.persistence.*

@Entity
@Table(name = "ticket_bans")
data class TicketBan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null
)
