package dev.slne.surf.discord.interaction.modal.impl.ticket.whitelist

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
class SurvivalWhitelistEditModal(
    private val whitelistService: WhitelistService
) : DiscordModal {
    override val id = "whitelist:modal:edit-survival"

    override suspend fun create(hook: InteractionHook, vararg data: String) =
        modal(id, translatable("whitelist.survival.edit.modal.title", data[0])) {
            textInput {
                id = "whitelist:modal:edit-survival:minecraft-name"
                label = translatable("whitelist.embed.information.minecraft")
                value = data[1]
                required = true
            }

            textInput {
                id = "whitelist:modal:edit-survival:discord-id"
                label = translatable("whitelist.embed.information.discord")
                value = data[2]
                required = true
            }

            selectMenu(
                translatable("whitelist.survival.edit.modal.blocked.label"),
                StringSelectMenu
                    .create("whitelist:select:survival-edit:blocked")
                    .addOption("Ja", "true")
                    .addOption("Nein", "false")
                    .setMaxValues(1)
                    .setMinValues(1)
                    .setDefaultOptions(SelectOption.of(data[3], data[4]))
                    .build()
            )
        }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val minecraftName =
            event.getValue("whitelist:modal:edit-survival:minecraft-name")?.asString ?: return
        val discordId =
            event.getValue("whitelist:modal:edit-survival:discord-id")?.asString?.toLongOrNull()
                ?: return
        val blocked =
            event.getValue("whitelist:select:survival-edit:blocked")?.asStringList?.first()
                ?.toBooleanStrictOrNull() ?: return

        val discordName = event.jda.getUserById(discordId)?.name ?: discordId.toString()


        whitelistService.updateWhitelist(discordId, minecraftName, blocked)

        event.reply(translatable("whitelist.embed.information.successfully-edited", discordName))
            .setEphemeral(true)
            .queue()
    }
}