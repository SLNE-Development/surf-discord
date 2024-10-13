package dev.slne.discord.discord.interaction.modal.step

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.components.StringSelectMenu
import dev.slne.discord.DiscordBot
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import java.util.concurrent.atomic.AtomicInteger

abstract class ModalSelectionStep(
    val selectTitle: String,
    vararg options: SelectOption
) : ModalStep() {

    private val id: String = generateId()
    private val options = options.toList()

    var selected: String? = null
    var event: StringSelectInteractionEvent? = null

    init {
        steps[id] = this
    }

    fun createSelection() = StringSelectMenu(id) {
        addOptions(this@ModalSelectionStep.options)
        maxValues = 1
    }

    private fun setSelected(selected: String, event: StringSelectInteractionEvent) {
        this.selected = selected
        this.event = event
    }

    object ModalSelectionStepListener {
        init {
            DiscordBot.jda.listener<StringSelectInteractionEvent> { event ->
                val step = steps[event.componentId] ?: return@listener
                val selected = event.values.first()

                step.setSelected(selected, event)
            }
        }
    }

    companion object {
        private val counter = AtomicInteger(0)
        private val steps = Object2ObjectOpenHashMap<String, ModalSelectionStep>()

        private fun generateId(): String {
            return "surf-selection-step-" + counter.getAndIncrement()
        }
    }
}
