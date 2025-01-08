package dev.slne.discord.discord.interaction.modal.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.TextInput
import dev.slne.discord.discord.interaction.modal.DiscordBasicModal
import dev.slne.discord.persistence.external.Whitelist
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal

const val CHANGE_WHITELIST_MODAL_ID = "change-whitelist"
private const val MINECRAFT_NAME = "minecraft-name"
private const val TWITCH_LINK = "twitch-link"
private const val DISCORD_USER = "discord-user"

class ChangeWhitelistModal(
    openingUser: User,
    private val userService: UserService,
    private val whitelistService: WhitelistService,
    private val whitelist: Whitelist,
    private val channel: MessageChannelUnion
) : DiscordBasicModal() {

    override val id = "$CHANGE_WHITELIST_MODAL_ID-${openingUser.id}-${whitelist.uuid}"

    override suspend fun buildModal(): Modal {
        val currentMinecraftName =
            userService.getUsernameByUuid(whitelist.uuid) ?: error("User not found")

        val minecraftNameInput = TextInput(
            MINECRAFT_NAME,
            "Minecraft Name",
            TextInputStyle.SHORT,
            true,
            currentMinecraftName,
            "CastCrafter"
        )

        val twitchLinkInput = TextInput(
            TWITCH_LINK,
            "Twitch Link",
            TextInputStyle.SHORT,
            true,
            whitelist.twitchLink,
            "CastCrafter"
        )

        val discordUserInput = TextInput(
            DISCORD_USER,
            "Discord User",
            TextInputStyle.SHORT,
            true,
            whitelist.discordId,
            "CastCrafter oder 128876960238665728"
        )

        return Modal.create(id, "Whitelist ändern")
            .addComponents(
                ActionRow.of(minecraftNameInput),
                ActionRow.of(twitchLinkInput),
                ActionRow.of(discordUserInput)
            )
            .build()
    }

    override suspend fun handleUserSubmitModal(
        event: ModalInteractionEvent,
        hook: InteractionHook
    ) {
        val minecraftName = getValueOrFail(event, hook, MINECRAFT_NAME) ?: return
        val twitchLink = getValueOrFail(event, hook, TWITCH_LINK) ?: return
        val discordUser = getValueOrFail(event, hook, DISCORD_USER) ?: return

        val newUuid = userService.getUuidByUsername(minecraftName)

        if (newUuid == null) {
            hook.editOriginal("Der angegebene Minecraft Name existiert nicht.").await()
            return
        }

        whitelist.apply {
            this.uuid = newUuid
            this.twitchLink = twitchLink
            this.discordId = discordUser
        }

        whitelistService.saveWhitelist(whitelist)

        event.reply("Whitelist erfolgreich geändert.").setEphemeral(true).await()
        channel.sendMessage("Whitelist für $minecraftName wurde geändert.").await()
    }

    private suspend fun getValueOrFail(
        event: ModalInteractionEvent,
        hook: InteractionHook,
        key: String
    ): String? {
        val mapping = event.getValue(key)

        if (mapping == null || mapping.asString.isBlank()) {
            hook.editOriginal("Bitte fülle alle Felder aus.").await()
            return null
        }

        return mapping.asString
    }
}