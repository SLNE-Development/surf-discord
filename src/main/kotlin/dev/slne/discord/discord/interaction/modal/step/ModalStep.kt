package dev.slne.discord.discord.interaction.modal.step

import lombok.Getter
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Blocking
import java.io.Serial
import java.util.*

/**
 * Represents a step in a modal process, which may include input validation, preparation, and
 * finalization.
 */
abstract class ModalStep {
    @Getter(lazy = true)
    private val children: LinkedList<ModalStep> = buildChildSteps().getSteps()

    /**
     * Builds the components for this modal step.
     *
     * @param builder The builder used to construct the modal components.
     */
    @ApiStatus.OverrideOnly
    protected abstract fun buildModalComponents(builder: ModalComponentBuilder)

    /**
     * Fills the provided builder with modal components for this step and its children.
     *
     * @param builder The builder to be filled with components.
     */
    fun fillModalComponents(builder: ModalComponentBuilder) {
        buildModalComponents(builder)
        getChildren().forEach { child -> child.fillModalComponents(builder) }
    }

    /**
     * Verifies the input provided in the modal interaction event for this step.
     *
     *
     * This method should be overridden by subclasses to implement specific input validation
     * logic.
     *
     * @param event The modal interaction event containing user input.
     * @throws ModalStepInputVerificationException if the input is invalid.
     */
    @Blocking
    @ApiStatus.OverrideOnly
    @Throws(
        ModalStepInputVerificationException::class
    )
    protected open fun verifyModalInput(event: ModalInteractionEvent) {
        // Override to implement input validation
    }

    /**
     * Runs the input verification for this step and all of its children.
     *
     * @param event The modal interaction event containing user input.
     * @throws ModalStepInputVerificationException if any input is invalid.
     */
    @Blocking
    @Throws(ModalStepInputVerificationException::class)
    fun runVerifyModalInput(event: ModalInteractionEvent) {
        verifyModalInput(event)
        for (child in getChildren()) {
            child.runVerifyModalInput(event)
        }
    }

    /**
     * Retrieves the required input from the modal interaction event by its key.
     *
     * @param event The modal interaction event.
     * @param key   The key identifying the input field.
     * @return The input value as a string.
     * @throws ModalStepInputVerificationException if the required input is missing.
     */
    @Throws(ModalStepInputVerificationException::class)
    protected fun getRequiredInput(event: ModalInteractionEvent, key: String): String {
        val value = event.getValue(key)
            ?: throw ModalStepInputVerificationException("Missing required input: $key")

        return value.asString
    }

    /**
     * Retrieves optional input from the modal interaction event by its key.
     *
     * @param event The modal interaction event.
     * @param key   The key identifying the input field.
     * @return The input value as a string, or null if not provided.
     */
    protected fun getOptionalInput(
        event: ModalInteractionEvent,
        key: String
    ): String? {
        val value = event.getValue(key) ?: return null

        return value.asString
    }

    /**
     * Prepares the channel creation process asynchronously.
     *
     *
     * This method should be overridden by subclasses to implement specific preparation logic.
     *
     * @throws ModuleStepChannelCreationException if an error occurs during preparation.
     */
    @Blocking
    @ApiStatus.OverrideOnly
    @Throws(
        ModuleStepChannelCreationException::class
    )
    protected fun prepareChannelCreationAsync() {
        // Override to implement preparation logic
    }

    /**
     * Runs the preparation logic for channel creation for this step and all of its children.
     *
     * @throws ModuleStepChannelCreationException if an error occurs during preparation.
     */
    @Throws(ModuleStepChannelCreationException::class)
    fun runPreChannelCreationAsync() {
        prepareChannelCreationAsync()
        for (child in getChildren()) {
            child.runPreChannelCreationAsync()
        }
    }

    /**
     * Builds the messages that should be displayed when the channel is opened.
     *
     *
     * This method should be overridden by subclasses to add custom messages.
     *
     * @param messages The message queue to which messages are added.
     * @param channel  The channel where the messages will be sent.
     */
    @ApiStatus.OverrideOnly
    protected open fun buildOpenMessages(messages: MessageQueue, channel: TextChannel?) {
        // Override to add custom open messages
    }

    /**
     * Retrieves the open messages for this step and all of its children.
     *
     * @param messages The message queue to which messages are added.
     * @param channel  The channel where the messages will be sent.
     */
    fun getOpenMessages(messages: MessageQueue, channel: TextChannel?) {
        buildOpenMessages(messages, channel)
        for (child in getChildren()) {
            child.getOpenMessages(messages, channel)
        }
    }

    /**
     * Executes custom logic after the channel has been created.
     *
     *
     * This method should be overridden by subclasses to add post-creation logic.
     *
     * @param channel The channel that was created.
     */
    @ApiStatus.OverrideOnly
    protected fun onPostChannelCreated(@Suppress("unused") channel: TextChannel?) {
    }

    /**
     * Runs the post-creation logic for this step and all of its children.
     *
     * @param channel The channel that was created.
     */
    fun runPostChannelCreated(channel: TextChannel?) {
        onPostChannelCreated(channel)
        for (child in getChildren()) {
            child.runPostChannelCreated(channel)
        }
    }

    /**
     * Builds the child steps for this modal step.
     *
     * @return A StepBuilder containing the child steps.
     */
    @ApiStatus.OverrideOnly
    open fun buildChildSteps(): StepBuilder {
        return StepBuilder.Companion.empty()
    }

    /**
     * Checks if this step or any of its children involve a selection process.
     *
     * @return true if this step or any child has a selection process, false otherwise.
     */
    fun hasSelectionStep(): Boolean {
        if (this is ModalSelectionStep) {
            return true
        }

        for (child in getChildren()) {
            if (child.hasSelectionStep()) {
                return true
            }
        }

        return false
    }

    /**
     * Exception thrown when a modal step's input verification fails.
     */
    @StandardException
    open class ModalStepInputVerificationException : Exception() {
        companion object {
            @Serial
            private const val serialVersionUID = 7427250171852391L
        }
    }

    /**
     * Exception thrown when a channel creation step fails.
     */
    @StandardException
    object ModuleStepChannelCreationException : ModalStepInputVerificationException() {
        @Serial
        private val serialVersionUID = -4690438162011591307L
    }
}
