package dev.slne.discordold.discord.interaction.command

import dev.slne.discordold.exception.command.CommandExceptions
import dev.slne.discordold.guild.getDiscordGuildByGuildId
import dev.slne.discordold.util.ExceptionFactory
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.interactions.Interaction

fun <T : Throwable> User.checkUserNotBot(
    exceptionFactory: ExceptionFactory<T>
) {
    if (this == jda.selfUser) {
        throw exceptionFactory.create()
    }
}

fun <T : Throwable> Member.checkMemberNotBot(
    exceptionFactory: ExceptionFactory<T>
) {
    user.checkUserNotBot(exceptionFactory)
}


fun Interaction.getGuildOrThrow() = guild ?: throw CommandExceptions.NO_GUILD.create()

fun Guild.getGuildConfig() = getDiscordGuildByGuildId(id)

fun Guild.getGuildConfigOrThrow() =
    getGuildConfig() ?: throw CommandExceptions.SERVER_NOT_REGISTERED.create()

fun Interaction.getThreadChannelOrThrow() =
    channel as? ThreadChannel ?: throw CommandExceptions.NO_THREAD_CHANNEL.create()
