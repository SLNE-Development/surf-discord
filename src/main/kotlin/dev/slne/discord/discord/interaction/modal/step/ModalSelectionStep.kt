package dev.slne.discord.discord.interaction.modal.step

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.interactions.components.StringSelectMenu
import dev.slne.discord.exception.step.modal.selection.ValidateModalSelectionException
import dev.slne.discord.getBean
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume

abstract class ModalSelectionStep(
    val selectTitle: String,
    vararg options: SelectOption
) : ModalStep() {

    private val id: String = generateId()
    private val options = options.toList()

    var selected: String? = null
    private var event: StringSelectInteractionEvent? = null
    private var onSelectionCallback: ((StringSelectInteractionEvent) -> Unit)? = null

    init {
        steps[id] = this
    }

    fun createSelection() = StringSelectMenu(id) {
        addOptions(this@ModalSelectionStep.options)
        maxValues = 1
    }

    @Throws(ValidateModalSelectionException::class)
    open suspend fun afterSelection(event: StringSelectInteractionEvent) {
        // Override this method to validate the selection
    }

    private fun setSelected(selected: String, event: StringSelectInteractionEvent) {
        this.selected = selected
        this.event = event
        onSelectionCallback?.invoke(event)
    }

    suspend fun awaitSelection(): StringSelectInteractionEvent =
        suspendCancellableCoroutine { continuation ->
            if (event != null) {
                continuation.resume(event!!)
            } else {
                onSelectionCallback = {
                    if (continuation.isActive) {
                        continuation.resume(it)
                    }
                }
            }
        }

    @Component
    class ModalSelectionStepListener {

        //        @PostConstruct TODO: Fixme
        fun registerListener() {
            getBean<JDA>().listener<StringSelectInteractionEvent> { event ->
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
