package dev.slne.surf.discord.interaction.modal.impl.ticket.whitelist

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.database.whitelist.WhitelistService
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import org.springframework.stereotype.Component

@Component
class SurvivalWhitelistDeleteModal(
    private val whitelistService: WhitelistService
) : DiscordModal {
    override val id = "whitelist:modal:delete-survival"

    override suspend fun create(hook: InteractionHook, vararg data: String) =
        modal(id, translatable("whitelist.survival.delete.modal.title", data[0])) {

            textInput {
                id = "whitelist:modal:delete-survival:minecraft-name"
                label = translatable("whitelist.embed.information.minecraft")
                value = data[1]
                required = true
            }

            textInput {
                id = "whitelist:modal:delete-survival:discord-id"
                label = translatable("whitelist.embed.information.discord")
                value = data[2]
                required = true
            }

            selectMenu(
                translatable("whitelist.survival.edit.modal.delete.label"),
                StringSelectMenu
                    .create("whitelist:select:survival-delete:confirm")
                    .addOption("Ja", "true")
                    .addOption("Nein", "false")
                    .setMaxValues(1)
                    .setMinValues(1)
                    .setDefaultOptions(SelectOption.of("Nein", "false"))
                    .build()
            )
        }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val discordId =
            event.getValue("whitelist:modal:delete-survival:discord-id")?.asString?.toLongOrNull()
                ?: return
        val deleteConfirmation =
            event.getValue("whitelist:select:survival-delete:confirm")?.asStringList?.first()
                ?.toBooleanStrictOrNull() ?: return

        if (!deleteConfirmation) {
            event.reply(translatable("whitelist.survival.delete.modal.cancelled"))
                .setEphemeral(true)
                .queue()
            return
        }

        whitelistService.deleteWhitelist(discordId)

        event.guild?.let { guild ->
            guild.getMemberById(discordId)?.let {
                guild.removeRoleFromMember(it,
                    guild.getRoleById(botConfig.whitelistedRoleId)
                        ?: error("Whitelisted role is null")
                ).queue()
            }
        }

        event.reply(translatable("whitelist.embed.information.successfully-deleted"))
            .setEphemeral(true)
            .queue()
    }
}