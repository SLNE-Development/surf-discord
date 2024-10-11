package dev.slne.discord.discord.interaction.modal.step

import com.google.common.base.Preconditions.checkState
import org.jetbrains.annotations.Contract
import java.util.*
import java.util.function.Function

/**
 * A builder class for creating a sequence of modal steps.
 */
class StepBuilder {
    @Getter
    private val steps = LinkedList<ModalStep?>()
    private var lastStep: ModalStep? = null

    private constructor(firstStep: ModalStep) {
        lastStep = firstStep
        steps.add(lastStep)
    }

    private constructor()

    /**
     * Adds a step to the sequence, using a function to generate the next step from the current one.
     *
     * @param step A function that generates the next step.
     * @return The current StepBuilder instance.
     */
    @Contract("_ -> this")
    fun then(step: Function<ModalStep?, ModalStep>): StepBuilder {
        checkState(lastStep != null, "Cannot add a step to an empty builder")

        lastStep = step.apply(lastStep)
        steps.add(lastStep)

        return this
    }

    @get:Contract(pure = true)
    val firstStep: ModalStep?
        /**
         * Retrieves the first step in the sequence.
         *
         * @return The first ModalStep in the sequence.
         */
        get() = steps.first

    companion object {
        /**
         * Starts the builder with the specified first step.
         *
         * @param firstStep The first step in the sequence.
         * @return A new StepBuilder instance.
         */
        @Contract("_ -> new")
        fun startWith(firstStep: ModalStep): StepBuilder {
            return StepBuilder(firstStep)
        }

        /**
         * Creates an empty StepBuilder.
         *
         * @return A new empty StepBuilder instance.
         */
        @Contract(" -> new")
        fun empty(): StepBuilder {
            return StepBuilder()
        }
    }
}
