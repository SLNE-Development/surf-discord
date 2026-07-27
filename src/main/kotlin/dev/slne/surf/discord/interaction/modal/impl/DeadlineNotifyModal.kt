package dev.slne.surf.discord.interaction.modal.impl

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.database.deadline.DeadlineNotifyRepository
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.modals.Modal

object DeadlineNotifyModal : DiscordModal {
    override val id = "deadline-notify"

    private val choiceId = "deadline-notify:choice"
    private val valueYes = "yes"
    private val valueNo = "no"

    fun build(currentlyEnabled: Boolean): Modal =
        modal(id, translatable("ticket.reply-deadline.notify.modal.title")) {
            selectMenu(
                translatable("ticket.reply-deadline.notify.modal.field.label"),
                description = translatable("ticket.reply-deadline.notify.modal.field.description"),
                selectMenu = StringSelectMenu.create(choiceId)
                    .addOptions(
                        SelectOption.of(
                            translatable("ticket.reply-deadline.notify.modal.option.yes"),
                            valueYes
                        ).withDefault(currentlyEnabled),
                        SelectOption.of(
                            translatable("ticket.reply-deadline.notify.modal.option.no"),
                            valueNo
                        ).withDefault(!currentlyEnabled)
                    )
                    .setRequiredRange(1, 1)
                    .build()
            )
        }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val choice = event.getValue(choiceId)?.asStringList?.firstOrNull() ?: return
        val enabled = choice == valueYes

        DeadlineNotifyRepository.setEnabled(event.user.idLong, enabled)

        event.replyEmbeds(embed {
            title = translatable("ticket.reply-deadline.notify.modal.title")
            description = translatable(
                if (enabled) "ticket.reply-deadline.notify.saved.enabled"
                else "ticket.reply-deadline.notify.saved.disabled"
            )
            color = if (enabled) Colors.SUCCESS else Colors.INFO
        }).setEphemeral(true).queue()
    }
}
