package dev.slne.discord.ticket

import dev.slne.discord.config.discord.GuildConfig
import dev.slne.discord.exception.ticket.DeleteTicketChannelException
import dev.slne.discord.exception.ticket.UnableToGetTicketNameException
import dev.slne.discord.exception.ticket.member.TicketAddMemberException
import dev.slne.discord.exception.ticket.member.TicketRemoveMemberException
import dev.slne.discord.guild.permission.TicketViewPermission
import dev.slne.discord.guild.role.DiscordRolePermissions
import dev.slne.discord.spring.service.ticket.TicketService
import dev.slne.discord.ticket.TicketChannelHelper.Companion.LOGGER
import dev.slne.discord.ticket.TicketChannelHelper.Companion.allPermissions
import dev.slne.discord.ticket.TicketChannelHelper.Companion.handleCreationException
import dev.slne.discord.ticket.member.TicketMember
import dev.slne.discord.ticket.result.TicketCreateResult
import it.unimi.dsi.fastutil.objects.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.concrete.Category
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.ChannelAction
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.math.min

/**
 * The type Ticket channel.
 */
@Component
@ParametersAreNonnullByDefault
object TicketChannelHelper @Autowired constructor(
    private val jda: JDA,
    ticketService: TicketService
) {
    private val ticketService: TicketService = ticketService

    /**
     * Adds a ticket member to the channel
     *
     * @param ticket       the ticket
     * @param ticketMember the ticket member
     * @return when completed
     */
    @Async
    @Throws(TicketAddMemberException::class)
    fun addTicketMember(ticket: Ticket, ticketMember: TicketMember?): CompletableFuture<Void> {
        val addedMember: TicketMember = ticketService.addTicketMember(ticket, ticketMember).join()
        val guild = ticket.guild
        val channel: TextChannel? = ticket.channel
        val userRest: RestAction<User> = addedMember.member


        if (guild == null || channel == null || userRest == null) {
            throw TicketAddMemberException("Guild, channel or user not found")
        }

        val manager = channel.manager
        val user = userRest.complete() ?: throw TicketAddMemberException("User not found")

        val member = guild.retrieveMember(user).submit().join()
            ?: throw TicketAddMemberException("Member not found")

        val memberDiscordRoles = member.roles
        val memberRoles: List<DiscordRolePermissions> = memberDiscordRoles.stream()
            .map<Any> { role: Role ->
                DiscordRolePermissions.getDiscordRoleRoles(
                    guild.id,
                    role.id
                )
            }
            .flatMap<Any> { obj: Any -> obj.stream() }
            .toList()

        val permissions: Set<Permission> = memberRoles.stream()
            .map<List<TicketViewPermission?>>(DiscordRolePermissions::ticketViewPermissions)
            .flatMap<TicketViewPermission?> { obj: List<TicketViewPermission?> -> obj.stream() }
            .map<String>(TicketViewPermission::permission)
            .collect<Set<Permission>, Any>(Collectors.toSet<Any>())

        return CompletableFuture.completedFuture(
            manager.putMemberPermissionOverride(user.idLong, permissions, null)
                .complete()
        )
    }

    /**
     * Removes a ticket member from the channel
     *
     * @param ticket       the ticket
     * @param ticketMember the ticket member
     * @return when completed
     */
    @Async
    @Throws(TicketRemoveMemberException::class)
    fun removeTicketMember(
        ticket: Ticket,
        member: TicketMember,
        remover: User
    ): CompletableFuture<Void> {
        val channel: TextChannel = ticket.channel
            .orElseThrow {}
        val user: User = member.getMemberNow()
            .orElseThrow {}

        ticketService.removeTicketMember(ticket, member, remover).join()

        val manager = channel.manager

        return CompletableFuture.completedFuture(
            manager.removePermissionOverride(user.idLong).complete()
        )
    }

    /**
     * Returns the default permission overrides for the ticket channel
     *
     * @param ticket The ticket
     * @param author The author of the ticket
     * @return The default permission overrides for the ticket channel
     */
    fun getChannelPermissions(ticket: Ticket, author: User): ObjectList<TicketPermissionOverride> {
        val guild = ticket.guild
        val ticketType = ticket.ticketType

        val allPermissions =
            allPermissions
        val overrides: ObjectList<TicketPermissionOverride> =
            ObjectArrayList<TicketPermissionOverride>()

        // Deny public role
        overrides.add(
            TicketPermissionOverride.builder()
                .type(Type.ROLE)
                .id(guild!!.publicRole.idLong)
                .allow(null)
                .deny(allPermissions)
                .build()
        )

        // Allow bot user
        overrides.add(
            TicketPermissionOverride.builder()
                .type(Type.USER)
                .id(jda.selfUser.idLong)
                .allow(allPermissions)
                .deny(null)
                .build()
        )

        // Allow bot role
        val botRole = guild.botRole
        if (botRole != null) {
            overrides.add(
                TicketPermissionOverride.builder()
                    .type(Type.ROLE)
                    .id(botRole.idLong)
                    .allow(allPermissions)
                    .deny(null)
                    .build()
            )
        }

        val roleConfigMap: Object2ObjectMap<String, DiscordRolePermissions> =
            GuildConfig.getByGuild(guild)
                .getRoleConfig()

        for (roleConfig in roleConfigMap.values) {
            if (roleConfig == null) {
                continue
            }

            val allowedPermissions: ObjectSet<Permission>?
            val deniedPermissions: ObjectSet<Permission>?

            if (roleConfig.canViewTicketType(ticketType)) {
                allowedPermissions = allPermissions
                deniedPermissions = null
            } else {
                allowedPermissions = null
                deniedPermissions = allPermissions
            }

            for (roleId in roleConfig.discordRoleIds) {
                val role = guild.getRoleById(roleId)

                if (role != null) {
                    overrides.add(
                        TicketPermissionOverride.builder()
                            .type(Type.ROLE)
                            .id(role.idLong)
                            .allow(allowedPermissions)
                            .deny(deniedPermissions)
                            .build()
                    )
                }
            }
        }

        val defaultRole: DiscordRolePermissions = DiscordRolePermissions.defaultRole

        // Apply author
        overrides.add(
            TicketPermissionOverride.builder()
                .type(Type.USER)
                .id(author.idLong)
                .deny(null)
                .allow(
                    defaultRole.ticketViewPermissions.stream()
                        .map<String>(TicketViewPermission::permission)
                        .toList()
                )
                .build()
        )

        return overrides
    }

    /**
     * Creates the author ticket member
     *
     * @param ticket        The ticket
     * @param author        The author of the ticket
     * @param channelAction the channel action
     * @return The result of the ticket member creation
     */
    @Async
    protected fun createAuthorTicketMember(
        ticket: Ticket,
        author: User,
        channelAction: ChannelAction<TextChannel>
    ): CompletableFuture<TicketMember> {
        val member: TicketMember = TicketMember(ticket, author, jda.selfUser)
        val defaultRole: DiscordRolePermissions = DiscordRolePermissions.defaultRole
        val allowedPermissions: List<Permission> = defaultRole.ticketViewPermissions
            .stream()
            .map<String>(TicketViewPermission::permission)
            .toList()

        channelAction.addMemberPermissionOverride(
            author.idLong,
            allowedPermissions,
            null
        )

        return ticket.addTicketMember(member)
    }

    /**
     * Create the ticket channel
     *
     * @param ticket     The ticket to create the channel for
     * @param ticketName The name of the ticket
     * @param category   The category to create the ticket in
     * @return The result of the ticket creation
     */
    @Async
    fun createTicketChannel(
        ticket: Ticket,
        ticketName: String,
        category: Category
    ): CompletableFuture<TicketCreateResult> {
        if (!ticket.hasGuild()) {
            return CompletableFuture.completedFuture<TicketCreateResult>(TicketCreateResult.GUILD_NOT_FOUND)
        }

        try {
            val channelAction = category.createTextChannel(ticketName)
            val author: User = ticket.getTicketAuthorNow()
            createAuthorTicketMember(ticket, author, channelAction).join()

            getChannelPermissions(ticket, author)
                .forEach(Consumer<TicketPermissionOverride> { override: TicketPermissionOverride ->
                    override.addOverride(
                        channelAction
                    )
                })

            val ticketChannel = channelAction.complete()
            ticket.threadId = ticketChannel.id
            ticketService.updateTicket(ticket).join()

            // Ablauf:
            //1. Channel erstellen
            // 2. Member adden
            // 3. Nachrichten senden (openening)
//      ticketChannel.createThreadChannel("Test", true)
//          .flatMap(threadChannel -> {
//            List<RestAction<Void>> actions = new ObjectArrayList<>();
//            actions.add(threadChannel.addThreadMember(author));
//
//            return RestAction.allOf(actions);
//          });
            return CompletableFuture.completedFuture<TicketCreateResult>(TicketCreateResult.SUCCESS)
        } catch (exception: Exception) {
            return CompletableFuture.completedFuture<TicketCreateResult>(
                handleCreationException(exception)
            )
        }
    }

    /**
     * Get the name for the ticket channel
     *
     * @param ticket The ticket to get the name for
     * @return The name for the ticket channel
     */
    @Async
    @Throws(UnableToGetTicketNameException::class)
    fun getTicketName(ticket: Ticket): CompletableFuture<String> {
        val ticketType = ticket.ticketType
        val author: User = ticket.getTicketAuthorNow()

        if (ticketType == null || author == null) {
            throw UnableToGetTicketNameException("Ticket type or author not found")
        }

        return CompletableFuture.completedFuture(generateTicketName(ticketType, author))
    }

    fun generateTicketName(
        expectedType: TicketType,
        expectedAuthor: User
    ): String {
        val ticketTypeName = expectedType.name.lowercase(Locale.getDefault())
        val authorName =
            expectedAuthor.name.lowercase(Locale.getDefault()).trim { it <= ' ' }.replace(" ", "-")
        val ticketName = "$ticketTypeName-$authorName"

        return ticketName.substring(
            0, min(
                ticketName.length.toDouble(),
                Channel.MAX_NAME_LENGTH.toDouble()
            ).toInt()
        )
    }

    /**
     * Deletes the ticket channel
     *
     * @param ticket The ticket to delete the channel for
     * @return The future result
     */
    @Async
    @Throws(DeleteTicketChannelException::class)
    fun deleteTicketChannel(ticket: Ticket): CompletableFuture<Void?> {
        val channel = ticket.channel
            ?: throw DeleteTicketChannelException("Channel not found")

        try {
            channel.delete().complete()
            ticketService.removeTicket(ticket)
        } catch (exception: Exception) {
            throw DeleteTicketChannelException("Failed to delete ticket channel", exception)
        }

        return CompletableFuture.completedFuture(null)
    }

    fun checkTicketExists(
        guild: Guild,
        expectedType: TicketType,
        expectedAuthor: User
    ): Boolean {
        val guildConfig: GuildConfig = GuildConfig.getByGuildId(guild.id)

        if (guildConfig == null) {
            LOGGER.error(
                "GuildConfig not found for guild {}. Preventing ticket creation.",
                guild.id
            )
            return true
        }

        val categoryId: String = guildConfig.getCategoryId()
        val channelCategory = guild.getCategoryById(categoryId)

        if (channelCategory == null) {
            LOGGER.error(
                "Category not found for guild {}. Preventing ticket creation.",
                guild.id
            )
            return true
        }

        return checkTicketExists(channelCategory, expectedType, expectedAuthor)
    }

    fun checkTicketExists(
        category: Category,
        expectedType: TicketType,
        expectedAuthor: User
    ): Boolean {
        return checkTicketExists(
            generateTicketName(expectedType, expectedAuthor),
            category,
            expectedType,
            expectedAuthor
        )
    }

    fun checkTicketExists(
        ticketChannelName: String?,
        category: Category,
        expectedType: TicketType,
        expectedAuthor: User
    ): Boolean {
        if (containsChannelName(category, ticketChannelName)) {
            return true
        }

        return hasAuthorTicketOfType(expectedType, expectedAuthor)
    }

    fun containsChannelName(category: Category, name: String?): Boolean {
        return category.channels.stream()
            .anyMatch { channel: GuildChannel -> channel.name.equals(name, ignoreCase = true) }
    }

    fun hasAuthorTicketOfType(type: TicketType, user: User): Boolean {
        return ticketService.getTickets().stream()
            .filter { ticket: Ticket -> ticket.ticketAuthorId == user.id }
            .anyMatch { ticket: Ticket -> ticket.ticketType == type }
    }

    companion object {
        private val LOGGER: ComponentLogger = ComponentLogger.logger("TicketChannelHelper")
        private val allPermissions: ObjectSet<Permission>
            /**
             * Returns all permissions
             *
             * @return all permissions
             */
            get() {
                val allPermissions: ObjectSet<Permission> =
                    ObjectArraySet()

                for (perm in Permission.entries) {
                    if (perm.isText) {
                        allPermissions.add(perm)
                    }
                }

                allPermissions.add(Permission.VIEW_CHANNEL)
                allPermissions.add(Permission.MANAGE_WEBHOOKS)
                allPermissions.add(Permission.MANAGE_CHANNEL)

                return allPermissions
            }

        private fun handleCreationException(throwable: Throwable): TicketCreateResult {
            if (throwable is ErrorResponseException
                && throwable.errorCode == 50013
            ) {
                return TicketCreateResult.MISSING_PERMISSIONS
            } else if (throwable is InsufficientPermissionException) {
                return TicketCreateResult.MISSING_PERMISSIONS
            } else {
                LOGGER.error("Failed to create ticket channel.", throwable)
                return TicketCreateResult.ERROR
            }
        }
    }
}
