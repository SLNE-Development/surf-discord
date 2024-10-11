package dev.slne.discord.discord.interaction.modal.step

import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import lombok.Getter
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

/**
 * Represents a step in a modal process that involves a selection component.
 */
@Getter
@Accessors(makeFinal = true)
abstract class ModalSelectionStep(private val selectTitle: String?, vararg options: SelectOption) :
    ModalStep() {
    private val id: String
    private val options: Array<SelectOption>

    private val selectionFuture = CompletableFuture<StringSelectInteractionEvent>()
    private var selected: String? = null

    init {
        this.options = options
        this.id = generateId()

        steps[id] = this
    }

    /**
     * Creates the selection component for this step.
     *
     * @return The created selection component.
     */
    fun createSelection(): ItemComponent {
        return StringSelectMenu.create(id)
            .addOptions(*options)
            .setMaxValues(1)
            .build()
    }

    /**
     * Sets the selected option for this step and completes the selection future.
     *
     * @param selected The selected option.
     * @param event    The selection event that triggered this action.
     */
    private fun setSelected(selected: String, event: StringSelectInteractionEvent) {
        this.selected = selected
        selectionFuture.complete(event)
    }

    /**
     * Listener class for handling selection interactions.
     */
    @DiscordListener
    class ModalSelectionStepListener : ListenerAdapter() {
        /**
         * Handles the selection interaction event and triggers the appropriate step.
         *
         * @param event The selection interaction event.
         */
        override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
            val step = steps[event.componentId] ?: return

            val selected: String = event.values.getFirst()
            step.setSelected(selected, event)
        }
    }

    companion object {
        private val counter = AtomicInteger(0)
        private val steps: Object2ObjectMap<String, ModalSelectionStep> = Object2ObjectOpenHashMap()

        /**
         * Generates a unique ID for this selection step.
         *
         * @return A unique ID string.
         */
        private fun generateId(): String {
            return "surf-selection-step-" + counter.getAndIncrement()
        }
    }
}
