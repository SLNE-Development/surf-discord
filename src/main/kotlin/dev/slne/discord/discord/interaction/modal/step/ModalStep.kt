package dev.slne.discord.discord.interaction.modal.step

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.jetbrains.annotations.ApiStatus
import java.io.Serial

abstract class ModalStep {
    val children: MutableList<ModalStep> by lazy { buildChildSteps().steps }

    @ApiStatus.OverrideOnly
    protected abstract fun buildModalComponents(builder: ModalComponentBuilder)

    fun fillModalComponents(builder: ModalComponentBuilder) {
        buildModalComponents(builder)

        children.forEach { child -> child.fillModalComponents(builder) }
    }

    @ApiStatus.OverrideOnly
    protected open suspend fun verifyModalInput(event: ModalInteractionEvent) {
        // Override to implement input validation
    }

    suspend fun runVerifyModalInput(event: ModalInteractionEvent) {
        verifyModalInput(event)

        children.forEach { it.runVerifyModalInput(event) }
    }

    protected fun getRequiredInput(event: ModalInteractionEvent, key: String) =
        event.getValue(key)?.asString
            ?: throw ModalStepInputVerificationException("Missing required input: $key")

    protected fun getOptionalInput(
        event: ModalInteractionEvent,
        key: String
    ) = event.getValue(key)?.asString

    @ApiStatus.OverrideOnly
    protected suspend fun prepareThreadCreationAsync() {
        // Override to implement preparation logic
    }

    suspend fun runPreThreadCreationAsync() {
        prepareThreadCreationAsync()

        children.forEach { it.runPreThreadCreationAsync() }
    }

    @ApiStatus.OverrideOnly
    protected open fun buildOpenMessages(messages: MessageQueue, thread: ThreadChannel) {
        // Override to add custom open messages
    }

    fun getOpenMessages(messages: MessageQueue, thread: ThreadChannel) {
        buildOpenMessages(messages, thread)

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
            @Serial
            private const val serialVersionUID = 7427250171852391L
        }
    }

    open class ModuleStepChannelCreationException(message: String) :
        ModalStepInputVerificationException(message) {
        companion object {
            @Serial
            private val serialVersionUID = -4690438162011591307L
        }
    }
}
