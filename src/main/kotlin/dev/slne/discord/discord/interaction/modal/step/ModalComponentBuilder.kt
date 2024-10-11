package dev.slne.discord.discord.interaction.modal.step

import lombok.Getter
import net.dv8tion.jda.api.interactions.components.ActionComponent
import org.jetbrains.annotations.Contract
import java.util.*

/**
 * A builder class for constructing modal components.
 */
@Getter
class ModalComponentBuilder {
    private val components = LinkedList<ActionComponent>()

    /**
     * Adds a component to the builder.
     *
     * @param component The component to add.
     * @return The current ModalComponentBuilder instance.
     */
    @Contract("_ -> this")
    fun addComponent(component: ActionComponent): ModalComponentBuilder {
        components.add(component)

        return this
    }

    /**
     * Adds a component to the beginning of the list.
     *
     * @param component The component to add.
     * @return The current ModalComponentBuilder instance.
     */
    @Contract("_ -> this")
    fun addFirstComponent(component: ActionComponent): ModalComponentBuilder {
        components.addFirst(component)

        return this
    }

    /**
     * Adds a component to the end of the list.
     *
     * @param component The component to add.
     * @return The current ModalComponentBuilder instance.
     */
    @Contract("_ -> this")
    fun addLastComponent(component: ActionComponent): ModalComponentBuilder {
        components.addLast(component)

        return this
    }

    /**
     * Adds a component at the specified index.
     *
     * @param index     The position to insert the component at.
     * @param component The component to add.
     * @return The current ModalComponentBuilder instance.
     */
    @Contract("_, _ -> this")
    fun addComponent(index: Int, component: ActionComponent): ModalComponentBuilder {
        components.add(index, component)

        return this
    }
}
