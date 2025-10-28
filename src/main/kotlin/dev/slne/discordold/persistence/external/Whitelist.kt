package dev.slne.discordold.persistence.external

import dev.slne.discordold.getBean
import jakarta.persistence.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.proxy.HibernateProxy
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(
    name = "freebuild_whitelists", uniqueConstraints = [
        UniqueConstraint(columnNames = ["uuid", "discord_id", "twitch_link"])
    ]
)
@Cacheable
data class Whitelist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "uuid", nullable = false, length = 36, unique = true)
    val uuid: UUID,

    @Column(name = "twitch_link")
    val twitchLink: String,

    @Column(name = "discord_id")
    val discordId: String,

    @Column(name = "added_by_id")
    val addedById: String? = null,

    @Column(name = "added_by_name")
    val addedByName: String? = null,

    @Column(name = "added_by_avatar_url")
    val addedByAvatarUrl: String? = null,

    @ColumnDefault("0")
    @Column(name = "blocked", nullable = false)
    var blocked: Boolean = false
) {

    constructor(uuid: UUID, twitchLink: String, discordId: String, addedBy: User?) : this(
        uuid = uuid,
        twitchLink = twitchLink,
        discordId = discordId,
        addedById = addedBy?.id,
        addedByName = addedBy?.name,
        addedByAvatarUrl = addedBy?.avatarUrl
    )

    val addedBy: RestAction<User>?
        get() = addedById?.let { getBean<JDA>().retrieveUserById(it) }

    val user: RestAction<User>?
        get() = getBean<JDA>().retrieveUserById(discordId)

    val clickableTwitchLink: String
        get() = "https://twitch.tv/$twitchLink"

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Whitelist

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return "Whitelist(id=$id, uuid=$uuid, twitchLink='$twitchLink', discordId='$discordId', blocked=$blocked, addedById=$addedById, addedByName=$addedByName, addedByAvatarUrl=$addedByAvatarUrl)"
    }
}