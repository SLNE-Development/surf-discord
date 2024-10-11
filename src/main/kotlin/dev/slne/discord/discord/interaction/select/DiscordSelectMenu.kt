package dev.slne.discord.discord.interaction.select

import dev.minn.jda.ktx.interactions.components.StringSelectMenu
import dev.minn.jda.ktx.interactions.components.option
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

abstract class DiscordSelectMenu(
    val id: String,
    private val placeholder: String,
    private val options: List<DiscordSelectMenuOption> = emptyList(),

    private val minValues: Int = 1,
    private val maxValues: Int = 1,
) {
    fun build() = StringSelectMenu(
        customId = id,
        placeholder = placeholder,
        valueRange = minValues..maxValues
    ) {
        for (option in this@DiscordSelectMenu.options) {
            option(
                label = option.label,
                value = option.value,
                description = option.description,
                emoji = option.emoji
            )
        }
    }

    abstract fun onSelect(
        interaction: StringSelectInteraction,
        selectedOptions: List<DiscordSelectMenuOption?>
    )

    fun getOptionByValue(value: String?) = options.firstOrNull { option -> option.value == value }

    data class DiscordSelectMenuOption(
        val label: String,
        val value: String,
        val description: String? = null,
        val emoji: Emoji? = null
    )
}

