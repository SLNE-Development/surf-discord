package dev.slne.discord.discord.interaction.modal

import lombok.Getter
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.modals.Modal

/**
 * The type Discord modal.
 */
@Getter
abstract class DiscordModal protected constructor(@field:Nonnull @param:Nonnull private val title: String) {
    @Nonnull
    protected val components: List<ActionComponent>

    /**
     * Creates a new DiscordModal.
     *
     * @param title The title of the modal.
     */
    init {
        this.components = ArrayList()
    }

    /**
     * Fills the components with the modal.
     */
    abstract fun fillComponents()

    /**
     * Executes the modal.
     *
     * @param event The event.
     */
    abstract fun execute(event: ModalInteractionEvent?)

    @get:Nonnull
    abstract val customId: String?

    /**
     * Builds the modal.
     *
     * @return The modal.
     */
    @Nonnull
    fun buildModal(): Modal {
        val modalBuilder: Modal.Builder = Modal.create(
            customId!!, this.title
        )

        fillComponents()

        if (!components.isEmpty()) {
            for (component: ActionComponent in this.components) {
                modalBuilder.addActionRow(component)
            }
        }

        return modalBuilder.build()
    }

    /**
     * Opens the modal.
     *
     * @param event The event.
     */
    @Suppress("unused")
    fun open(event: SlashCommandInteractionEvent) {
        val modal: Modal = this.buildModal()
        event.replyModal(modal).queue()
    }
}
