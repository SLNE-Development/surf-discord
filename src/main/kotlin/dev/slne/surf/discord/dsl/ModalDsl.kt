package dev.slne.surf.discord.dsl

import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal

@DslMarker
annotation class ModalDsl

@ModalDsl
class ModalBuilder(val id: String, var title: String) {
    private val rows = mutableListOf<ActionRow>()

    fun field(block: TextInputBuilder.() -> Unit) {
        val input = TextInputBuilder().apply(block).build()
        rows.add(ActionRow.of(input))
    }

    fun build(): Modal = Modal.create(id, title)
        .addComponents(rows)
        .build()
}

@ModalDsl
class TextInputBuilder {
    var id: String = "field"
    var label: String = "Label"
    var style: TextInputStyle = TextInputStyle.SHORT
    var placeholder: String? = null
    var required: Boolean = true
    var lengthRange: IntRange? = null
    var value: String? = null

    fun build(): TextInput {
        val builder = TextInput.create(id, label, style)
        placeholder?.let { builder.setPlaceholder(it) }
        lengthRange?.let {
            builder.minLength = it.first
            builder.maxLength = it.last
        }
        value?.let {
            builder.value = it
        }
        builder.setRequired(required)
        return builder.build()
    }
}

fun modal(id: String, title: String, block: ModalBuilder.() -> Unit): Modal {
    return ModalBuilder(id, title).apply(block).build()
}
