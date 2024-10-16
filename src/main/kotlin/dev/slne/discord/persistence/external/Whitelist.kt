package dev.slne.discord.persistence.external

import dev.slne.discord.DiscordBot
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistRepository
import jakarta.persistence.*
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(
    name = "freebuild_whitelists", uniqueConstraints = [
        UniqueConstraint(columnNames = ["uuid", "discord_id", "twitch_link"])
    ]
)

open class Whitelist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "uuid", nullable = false, length = 36)
    open var uuid: UUID? = null

    @Column(name = "twitch_link")
    open var twitchLink: String? = null

    @Column(name = "discord_id")
    open var discordId: String? = null

    @Column(name = "added_by_id")
    open var addedById: String? = null

    @Column(name = "added_by_name")
    open var addedByName: String? = null

    @Column(name = "added_by_avatar_url")
    open var addedByAvatarUrl: String? = null

    @ColumnDefault("0")
    @Column(name = "blocked", nullable = false)
    open var blocked: Boolean? = false

    val addedBy: RestAction<User>?
        get() = addedById?.let { DiscordBot.jda.retrieveUserById(it) }

    val user: RestAction<User>?
        get() = discordId?.let { DiscordBot.jda.retrieveUserById(it) }

    val clickableTwitchLink: String?
        get() = twitchLink?.let { "https://twitch.tv/$it" }

    suspend fun minecraftName(): String? = UserService.getUsernameByUuid(uuid!!)

    suspend fun save() = WhitelistRepository.saveWhitelist(this)
}