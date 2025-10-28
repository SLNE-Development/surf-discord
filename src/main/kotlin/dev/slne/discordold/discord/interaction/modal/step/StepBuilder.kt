package dev.slne.discordold.discord.interaction.modal.step

class StepBuilder {
    val steps = mutableListOf<ModalStep>()
    private var lastStep: ModalStep? = null

    private constructor(firstStep: ModalStep) {
        lastStep = firstStep

        steps.add(firstStep)
    }

    private constructor()

    fun then(step: (ModalStep) -> ModalStep) = apply {
        val lastStep = lastStep
        check(lastStep != null) { "Cannot add a step to an empty builder" }

        this.lastStep = step(lastStep)
        steps.add(this.lastStep!!)
    }

    val firstStep: ModalStep?
        get() = steps.firstOrNull()

    companion object {
        fun startWith(firstStep: ModalStep) = StepBuilder(firstStep)
        fun empty() = StepBuilder()
    }
}
