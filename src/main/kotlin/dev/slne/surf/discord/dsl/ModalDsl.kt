package dev.slne.surf.discord.dsl

import net.dv8tion.jda.api.components.label.Label
import net.dv8tion.jda.api.components.selections.SelectMenu
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.modals.Modal

@DslMarker
annotation class ModalDsl

@ModalDsl
class ModalBuilder(val id: String, var title: String) {
    private val rows = mutableListOf<Label>()

    fun textInput(block: TextInputBuilder.() -> Unit) {
        val input = TextInputBuilder().apply(block)

        rows.add(Label.of(input.label, input.build()))
    }

    fun selectMenu(label: String, selectMenu: SelectMenu) {
        rows.add(Label.of(label, selectMenu))
    }

    fun build(): Modal = Modal.create(id, title)
        .addComponents(rows).build()
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
        val builder = TextInput.create(id, style)

        placeholder?.let { builder.setPlaceholder(it) }
        lengthRange?.let {
            builder.minLength = it.first
            builder.maxLength = it.last
        }
        value?.let {
            builder.value = it
        }

        builder.isRequired = required

        return builder.build()
    }
}

fun modal(id: String, title: String, block: ModalBuilder.() -> Unit) =
    ModalBuilder(id, title).apply(block).build()
