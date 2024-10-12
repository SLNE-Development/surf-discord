package dev.slne.discord.discord.interaction.modal.step

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.components.StringSelectMenu
import dev.minn.jda.ktx.interactions.components.option
import dev.slne.discord.DiscordBot
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import java.util.concurrent.atomic.AtomicInteger

abstract class ModalSelectionStep(
    val selectTitle: String,
    vararg options: SelectOption
) : ModalStep() {

    private val id: String = generateId()
    var selected: String? = null
    private val options = options.toList()
    var event: StringSelectInteractionEvent? = null

    init {
        steps[id] = this
        // FIXME: 12.10.2024 08:51 test this and show to twisti?
    }

    fun createSelection(): ItemComponent = StringSelectMenu(id) {
        this@ModalSelectionStep.options.forEach {
            option(it.label, it.value, it.emoji?.formatted)
        }

        this.maxValues = 1
    }

    private fun setSelected(selected: String, event: StringSelectInteractionEvent) {
        this.selected = selected
        this.event = event
    }

    object ModalSelectionStepListener : ListenerAdapter() {
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
        private val steps: Object2ObjectMap<String, ModalSelectionStep> = Object2ObjectOpenHashMap()

        private fun generateId(): String {
            return "surf-selection-step-" + counter.getAndIncrement()
        }
    }
}
