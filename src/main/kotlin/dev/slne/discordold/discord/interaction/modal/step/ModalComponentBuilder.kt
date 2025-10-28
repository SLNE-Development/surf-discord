package dev.slne.discordold.discord.interaction.modal.step

import net.dv8tion.jda.api.interactions.components.ActionComponent

class ModalComponentBuilder {
    private val _components = mutableListOf<ActionComponent>()
    val components: List<ActionComponent>
        get() = _components.toList()

    fun addComponent(component: ActionComponent) = apply { _components.add(component) }
    fun addFirstComponent(component: ActionComponent) = apply { _components.addFirst(component) }
    fun addLastComponent(component: ActionComponent) = apply { _components.addLast(component) }
    fun addComponent(index: Int, component: ActionComponent) =
        apply { _components.add(index, component) }
}
