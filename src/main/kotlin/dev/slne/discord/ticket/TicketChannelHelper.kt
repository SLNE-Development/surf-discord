package dev.slne.discord.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.exception.ticket.DeleteTicketChannelException
import dev.slne.discord.exception.ticket.UnableToGetTicketNameException
import dev.slne.discord.exception.ticket.member.TicketAddMemberException
import dev.slne.discord.exception.ticket.member.TicketRemoveMemberException
import dev.slne.discord.guild.getDiscordGuildByGuildId
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.ticket.result.TicketCreateResult
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import kotlin.math.min


object TicketChannelHelper {

    private val logger = ComponentLogger.logger()

    suspend fun addTicketMember(ticket: Ticket, member: Member) {
        val thread = ticket.thread ?: throw TicketAddMemberException("Thread not found")

        thread.addThreadMember(member).await()
    }

    suspend fun addTicketMember(ticket: Ticket, user: User) = addTicketMember(
        ticket,
        ticket.guild?.retrieveMember(user)?.await()
            ?: throw TicketAddMemberException("Member not found")
    )

    suspend fun removeTicketMember(ticket: Ticket, member: Member) {
        val thread = ticket.thread ?: throw TicketRemoveMemberException("Thread not found")

        thread.removeThreadMember(member).await()
    }

    suspend fun removeTicketMember(ticket: Ticket, user: User) = removeTicketMember(
        ticket,
        ticket.guild?.retrieveMember(user)?.await()
            ?: throw TicketRemoveMemberException("Member not found")
    )

    suspend fun removeTicketRole(ticket: Ticket, role: Role) {
        ticket.thread?.permissionContainer?.upsertPermissionOverride(role)
            ?.deny(Permission.VIEW_CHANNEL)?.await()
    }

    suspend fun createThread(
        ticket: Ticket,
        ticketName: String,
        ticketChannel: TextChannel
    ): TicketCreateResult {
        val thread = ticketChannel.createThreadChannel(ticketName, true)
            .setInvitable(false)
            .await()
        ticket.threadId = thread.id

        val guild = ticket.guild ?: return TicketCreateResult.GUILD_NOT_FOUND
        val discordGuild = getDiscordGuildByGuildId(guild.id)?.discordGuild
            ?: return TicketCreateResult.GUILD_CONFIG_NOT_FOUND

        val roleIds = discordGuild.roles.filter { it.canViewTicketType(ticket.ticketType) }
            .flatMap { it.discordRoleIds }


        val pingParty = buildString {
            roleIds.forEach { roleId ->
                val role = guild.getRoleById(roleId) ?: return TicketCreateResult.ROLE_NOT_FOUND
                append(role.asMention)
            }
        }

        val pingPartyMessage = thread.sendMessage(translatable("common.waiting.for.ping")).await()
        pingPartyMessage.editMessage(pingParty).await()
        pingPartyMessage.delete().await()

//        for (member in guild.findMembersWithRoles(roleIds.mapNotNull { guild.getRoleById(it) })
//            .await()) {
//            thread.addThreadMember(member).await()
//        }

        thread.addThreadMember(
            ticket.author?.await() ?: return TicketCreateResult.AUTHOR_NOT_FOUND
        ).await()

        ticket.save()

        return TicketCreateResult.SUCCESS
    }

    suspend fun getTicketName(ticket: Ticket): String {
        val ticketType = ticket.ticketType
        val author = ticket.author?.await()
            ?: throw UnableToGetTicketNameException("Ticket author not found")

        return generateTicketName(ticketType!!, author)
    }

    fun generateTicketName(
        ticketType: TicketType,
        expectedAuthor: User
    ): String {
        val ticketTypeName = ticketType.name.lowercase()
        val authorName =
            expectedAuthor.name.lowercase().trim().replace(" ", "-")
        val ticketName = "$ticketTypeName-$authorName"

        return ticketName.substring(0, min(ticketName.length, Channel.MAX_NAME_LENGTH))
    }

    suspend fun closeThread(ticket: Ticket) {
        val thread = ticket.thread ?: throw DeleteTicketChannelException("Channel not found")

        try {
            thread.manager
                .setLocked(true)
                .setArchived(true)
                .await()

            TicketService.removeTicket(ticket)
        } catch (exception: Exception) {
            throw DeleteTicketChannelException("Failed to delete ticket channel", exception)
        }
    }

    fun checkTicketExists(
        guild: Guild,
        expectedType: TicketType,
        expectedAuthor: User
    ): Boolean {
        val guildConfig = getDiscordGuildByGuildId(guild.id)?.discordGuild

        if (guildConfig == null) {
            logger.error(
                "GuildConfig not found for guild {}. Preventing ticket creation.",
                guild.id
            )
            return true
        }

        val channelId = guildConfig.ticketChannels[expectedType]

        if (channelId == null) {
            logger.error(
                "Category not found for guild {}. Preventing ticket creation.",
                guild.id
            )
            return true
        }

        val channel = guild.getTextChannelById(channelId)

        if (channel == null) {
            logger.error(
                "Category not found for guild {}. Preventing ticket creation.",
                guild.id
            )
            return true
        }

        return checkTicketExists(channel, expectedType, expectedAuthor)
    }

    fun checkTicketExists(
        channel: TextChannel,
        expectedType: TicketType,
        expectedAuthor: User
    ): Boolean {
        return checkTicketExists(
            generateTicketName(expectedType, expectedAuthor),
            channel,
            expectedType,
            expectedAuthor
        )
    }

    fun checkTicketExists(
        ticketChannelName: String?,
        channel: TextChannel,
        expectedType: TicketType,
        expectedAuthor: User
    ): Boolean {
        if (containsActiveChannelName(channel, ticketChannelName)) {
            return true
        }

        return hasAuthorTicketOfType(expectedType, expectedAuthor)
    }

    private fun containsActiveChannelName(channel: TextChannel, name: String?) =
        channel.threadChannels.asSequence()
            .filter { !it.isArchived }
            .any { it.name.equals(name, ignoreCase = true) }


    private fun hasAuthorTicketOfType(type: TicketType, user: User) =
        TicketService.tickets.asSequence()
            .filter { !it.isClosed }
            .filter { it.ticketAuthorId == user.id }
            .any { it.ticketType == type }
}
