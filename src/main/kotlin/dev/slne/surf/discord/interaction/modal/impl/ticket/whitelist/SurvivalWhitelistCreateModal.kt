package dev.slne.surf.discord.interaction.modal.impl.ticket.whitelist

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.database.whitelist.SocialService
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class SurvivalWhitelistCreateModal(
    private val socialService: SocialService
) : DiscordModal {
    override val id = "whitelist:modal:create-survival"

    override fun create() = modal(id, translatable("whitelist.survival.modal.title")) {
        textInput {
            id = "minecraft_username"
            label = translatable("whitelist.survival.modal.label.username")
            style = TextInputStyle.SHORT
            required = true
            lengthRange = 3..16
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val minecraftUsername = event.getValue("minecraft_username")?.asString ?: return
        val discordId = event.user.idLong

        event.reply(translatable("whitelist.survival.modal.processing")).setEphemeral(true).queue()

        if (socialService.isWhitelisted(minecraftUsername)) {
            event.hook.editOriginal(translatable("whitelist.survival.modal.username_taken"))
                .queue()
            return
        }

        if (socialService.whitelist(discordId, minecraftUsername) == null) {
            event.hook.editOriginal(translatable("whitelist.survival.modal.user-not-found"))
                .queue()
            return
        }

        event.member?.let {
            event.guild?.addRoleToMember(
                it,
                event.guild?.getRoleById(botConfig.whitelistedRoleId)
                    ?: error("Whitelisted role not found")
            )?.queue()
        }

        event.hook.editOriginal(translatable("whitelist.survival.modal.success"))
            .queue()
    }
}