package dev.slne.discord.spring.feign.dto

import dev.slne.discord.DiscordBot
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.spring.service.whitelist.WhitelistService
import dev.slne.discord.util.TimeUtils
import jakarta.annotation.Nonnull
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.CompletableFuture

class WhitelistDTO(
    val id: Long = 0,
    val uuid: UUID? = null,
    val minecraftName: String? = null,
    val twitchLink: String? = null,
    val discordId: String? = null,
    val addedById: String? = null,
    val addedByName: String? = null,
    val addedByAvatarUrl: String? = null,
    var blocked: Boolean = false,
    val createdAt: ZonedDateTime? = null,
) {
    val addedBy: RestAction<User>?
        get() = addedById?.let { DiscordBot.jda.retrieveUserById(it) }

    val discordUser: RestAction<User>?
        get() = discordId?.let { DiscordBot.jda.retrieveUserById(it) }

    companion object {
        @JvmStatic
        fun createFrom(
            uuid: UUID,
            minecraftName: String?,
            twitchLink: String?,
            user: User?,
            executor: User?
        ): WhitelistDTO {
            val builder: WhitelistDTOBuilder = builder()
                .uuid(uuid)
                .minecraftName(minecraftName)
                .twitchLink(twitchLink)

            if (user != null) {
                builder.discordId(user.id)
            }

            if (executor != null) {
                builder.addedById(executor.id)
                    .addedByName(executor.name)
                    .addedByAvatarUrl(executor.avatarUrl)
            }

            return builder.build()
        }

        /**
         * Returns a [MessageEmbed] for a [WhitelistDTO].
         *
         * @param whitelist The [WhitelistDTO].
         * @return The [MessageEmbed].
         */
        @Nonnull
        fun getWhitelistQueryEmbed(
            whitelist: WhitelistDTO
        ): CompletableFuture<MessageEmbed> {
            val future: CompletableFuture<MessageEmbed> = CompletableFuture<MessageEmbed>()

            val builder: EmbedBuilder = EmbedBuilder()

            builder.setTitle("Whitelist Query")
            builder.setFooter(
                "Whitelist Query",
                DiscordBot.getInstance().getJda().getSelfUser().getAvatarUrl()
            )
            builder.setDescription("Whitelist Informationen")
            builder.setColor(EmbedColors.WL_QUERY)
            builder.setTimestamp(TimeUtils.berlinTimeProvider().getCurrentTime())

            DataApi.getNameByPlayerUuid(whitelist.getUuid()).thenAcceptAsync { name ->
                val uuid: UUID = whitelist.getUuid()
                val twitchLink: String = whitelist.getTwitchLink()
                val discordUserRest: RestAction<User>? =
                    whitelist.discordUser
                val addedByRest: RestAction<User>? = whitelist.addedBy

                var discordUserFuture =
                    CompletableFuture<User?>()
                var addedByFuture =
                    CompletableFuture<User?>()

                if (discordUserRest != null) {
                    discordUserFuture = discordUserRest.submit()
                } else {
                    discordUserFuture.complete(null)
                }

                if (addedByRest != null) {
                    addedByFuture = addedByRest.submit()
                } else {
                    addedByFuture.complete(null)
                }

                val finaldiscordUserFuture =
                    discordUserFuture
                val finalAddedByFuture =
                    addedByFuture
                CompletableFuture.allOf(
                    finaldiscordUserFuture,
                    finalAddedByFuture
                ).thenAccept { v: Void? ->
                    val discordUser =
                        finaldiscordUserFuture.join()
                    val addedBy = finalAddedByFuture.join()

                    if (name != null) {
                        builder.addField("Minecraft Name", name, true)
                    }

                    if (twitchLink != null) {
                        builder.addField("Twitch Link", twitchLink, true)
                    }

                    if (discordUser != null) {
                        builder.addField("Discord User", discordUser.asMention, true)
                    }

                    if (addedBy != null) {
                        builder.addField("Added By", addedBy.asMention, true)
                    }

                    if (uuid != null) {
                        builder.addField("UUID", uuid.toString(), false)
                    }
                    future.complete(builder.build())
                }
                    .exceptionally { exception: Throwable? ->
                        future.completeExceptionally(exception)
                        null
                    }
            }

            return future
        }
    }
}
