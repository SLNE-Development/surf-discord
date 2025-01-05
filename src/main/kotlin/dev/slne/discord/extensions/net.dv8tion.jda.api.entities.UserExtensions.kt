package dev.slne.discord.extensions

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.Method
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.Route
import net.dv8tion.jda.api.utils.data.DataArray
import net.dv8tion.jda.api.utils.data.DataObject
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.requests.RestActionImpl
import java.util.function.BiFunction

fun User.retrieveConnections(): RestAction<List<UserConnection>> {
    val requester = (jda as JDAImpl).requester

    val route = Route.custom(Method.GET, "users/{user_id}/connections")
        .compile(id)

    println("Route: $route")
    println("Compiled: ${route.compiledRoute}")

    return RestActionImpl<List<UserConnection>>(
        jda,
        route,
        BiFunction { response, _ ->
            response.array.stream(DataArray::getObject).map { UserConnection(it) }.toList()
        })
}

data class UserConnection(
    val id: String,
    val name: String,
    val type: String,
    val verified: Boolean,
    val friendSync: Boolean,
    val showActivity: Boolean,
    val twoWayLink: Boolean,
    val visibility: Visibility
) {
    constructor(data: DataObject) : this(
        id = data.getString("id"),
        name = data.getString("name"),
        type = data.getString("type"),
        verified = data.getBoolean("verified"),
        friendSync = data.getBoolean("friend_sync"),
        showActivity = data.getBoolean("show_activity"),
        twoWayLink = data.getBoolean("two_way_link"),
        visibility = Visibility.fromVisibility(data.getInt("visibility"))
    )

    enum class Visibility {
        NONE,
        EVERYONE;

        companion object {
            fun fromVisibility(visibility: Int): Visibility {
                return when (visibility) {
                    0 -> NONE
                    1 -> EVERYONE
                    else -> error("Unknown visibility: $visibility")
                }
            }
        }
    }
}