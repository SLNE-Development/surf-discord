package dev.slne.discord.ticket.member

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import dev.slne.discord.DiscordBot
import dev.slne.discord.ticket.Ticket
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import java.util.*

class TicketMember {

    @JsonProperty("id")
    var id: Long = 0

    @JsonProperty("member_id")
    var memberId: String? = null

    @JsonProperty("member_name")
    var memberName: String? = null

    @JsonProperty("member_avatar_url")
    var memberAvatarUrl: String? = null

    @JsonProperty("added_by_id")
    var addedById: String? = null

    @JsonProperty("added_by_name")
    var addedByName: String? = null

    @JsonProperty("added_by_avatar_url")
    var addedByAvatarUrl: String? = null

    @JsonProperty("removed_by_id")
    var removedById: String? = null

    @JsonProperty("removed_by_name")
    var removedByName: String? = null

    @JsonProperty("removed_by_avatar_url")
    var removedByAvatarUrl: String? = null

    @JsonProperty("ticket_id")
    var ticketId: UUID? = null

    @get:JsonIgnore
    val isRemoved: Boolean
        get() = removedBy != null || removedById != null || removedByName != null || removedByAvatarUrl != null

    @get:JsonIgnore
    val isActivated: Boolean
        get() = !isRemoved

    @get:JsonIgnore
    val ticket: Ticket
        get() = DiscordBot.getTicketManager().getTicketById(ticketId)

    @get:JsonIgnore
    val member: RestAction<User>?
        get() = memberId?.let { DiscordBot.jda.retrieveUserById(it) }

    @get:JsonIgnore
    val addedBy: RestAction<User>?
        get() = addedById?.let { DiscordBot.jda.retrieveUserById(it) }

    @get:JsonIgnore
    val removedBy: RestAction<User>?
        get() = removedById?.let { DiscordBot.jda.retrieveUserById(it) }
}

fun Ticket.createTicketMember(member: User?, addedBy: User?): TicketMember = TicketMember().apply {
    memberId = member?.id
    memberName = member?.name
    memberAvatarUrl = member?.avatarUrl
    addedById = addedBy?.id
    addedByName = addedBy?.name
    addedByAvatarUrl = addedBy?.avatarUrl
    this@apply.ticketId = this@createTicketMember.ticketId
}
