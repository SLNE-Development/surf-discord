package dev.slne.discord.discord.interaction.modal.step

import net.dv8tion.jda.api.interactions.components.ActionComponent

class ModalComponentBuilder {
    val components = mutableListOf<ActionComponent>()

    fun addComponent(component: ActionComponent): ModalComponentBuilder {
        components.add(component)

        return this
    }

    fun addFirstComponent(component: ActionComponent): ModalComponentBuilder {
        components.addFirst(component)

        return this
    }

    fun addLastComponent(component: ActionComponent): ModalComponentBuilder {
        components.addLast(component)

        return this
    }

    fun addComponent(index: Int, component: ActionComponent): ModalComponentBuilder {
        components.add(index, component)

        return this
    }
}
