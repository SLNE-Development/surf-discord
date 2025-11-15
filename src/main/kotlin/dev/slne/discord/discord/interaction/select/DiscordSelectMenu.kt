package dev.slne.discord.discord.interaction.select

import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

abstract class DiscordSelectMenu(
    val id: String,
    private val placeholder: String,
    private val options: List<SelectOption> = emptyList(),

    private val valueRange: IntRange
) {
    fun build() = StringSelectMenu.create(id)
        .setPlaceholder(placeholder)
        .setRequiredRange(valueRange.first, valueRange.last)
        .addOptions(options)
        .build()

    abstract suspend fun onSelect(
        interaction: StringSelectInteraction,
        selectedOptions: List<SelectOption>
    )

    fun getOptionByValue(value: String?) = options.firstOrNull { option -> option.value == value }
}

