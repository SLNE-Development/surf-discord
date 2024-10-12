package dev.slne.discord.discord.interaction.modal.step

import java.util.function.Function

class StepBuilder {

    val steps: MutableList<ModalStep> = mutableListOf()
    var lastStep: ModalStep? = null

    private constructor(firstStep: ModalStep) {
        lastStep = firstStep

        steps.add(firstStep)
    }

    private constructor()

    fun then(step: Function<ModalStep, ModalStep>): StepBuilder {
        check(lastStep != null) { "Cannot add a step to an empty builder" }

        lastStep = step.apply(lastStep!!)
        steps.add(lastStep!!)

        return this
    }

    val firstStep: ModalStep?
        get() = steps.first()

    companion object {
        fun startWith(firstStep: ModalStep): StepBuilder {
            return StepBuilder(firstStep)
        }

        fun empty(): StepBuilder {
            return StepBuilder()
        }
    }
}
