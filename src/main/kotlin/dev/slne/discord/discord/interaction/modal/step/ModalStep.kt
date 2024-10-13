package dev.slne.discord.discord.interaction.modal.step

import dev.minn.jda.ktx.interactions.components.InlineModal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.jetbrains.annotations.ApiStatus
import java.io.Serial

abstract class ModalStep {
    val children: MutableList<ModalStep> by lazy { buildChildSteps().steps }

    @ApiStatus.OverrideOnly
    protected abstract fun InlineModal.buildModalComponents()

    fun fillModalComponents(modal: InlineModal) {
        modal.buildModalComponents()

        for (child in children) {
            child.fillModalComponents(modal)
        }
    }

    @ApiStatus.OverrideOnly
    protected open suspend fun verifyModalInput(event: ModalInteractionEvent) {
        // Override to implement input validation
    }

    suspend fun runVerifyModalInput(event: ModalInteractionEvent) {
        verifyModalInput(event)

        children.forEach { it.runVerifyModalInput(event) }
    }

    protected fun getOptionalInput(
        event: ModalInteractionEvent,
        key: String
    ) = event.getValue(key)?.asString

    protected fun getInput(event: ModalInteractionEvent, key: String) =
        getOptionalInput(event, key)
            ?: throw ModalStepInputVerificationException("Missing required input: $key")

    protected operator fun ModalInteractionEvent.get(key: String) = getInput(this, key)

    @ApiStatus.OverrideOnly
    protected suspend fun prepareThreadCreation() {
        // Override to implement preparation logic
    }

    suspend fun runPreThreadCreation(): Unit = withContext(Dispatchers.IO) {
        prepareThreadCreation()

        children.forEach { it.runPreThreadCreation() }
    }

    @ApiStatus.OverrideOnly
    protected open fun MessageQueue.buildOpenMessages(thread: ThreadChannel) {
        // Override to add custom open messages
    }

    fun getOpenMessages(messages: MessageQueue, thread: ThreadChannel) {
        messages.buildOpenMessages(thread)

        children.forEach { it.getOpenMessages(messages, thread) }
    }

    @ApiStatus.OverrideOnly
    protected fun onPostThreadCreated(thread: ThreadChannel) {
    }

    fun runPostThreadCreated(thread: ThreadChannel) {
        onPostThreadCreated(thread)

        children.forEach { it.runPostThreadCreated(thread) }
    }

    @ApiStatus.OverrideOnly
    open fun buildChildSteps() = StepBuilder.empty()

    fun hasSelectionStep(): Boolean {
        if (this is ModalSelectionStep) {
            return true
        }

        for (child in children) {
            if (child.hasSelectionStep()) {
                return true
            }
        }

        return false
    }

    open class ModalStepInputVerificationException(message: String) : Exception(message) {
        companion object {
            @JvmStatic
            @Serial
            private val serialVersionUID = 7427250171852391L
        }
    }

    open class ModuleStepChannelCreationException(message: String) :
        ModalStepInputVerificationException(message) {
        companion object {
            @JvmStatic
            @Serial
            private val serialVersionUID = -4690438162011591307L
        }
    }
}
