package dev.slne.surf.discord.interaction.selectmenu.impl.feedback

import dev.slne.surf.discord.feedback.FeedbackCategory
import dev.slne.surf.discord.interaction.selectmenu.DiscordSelectMenu
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import org.springframework.stereotype.Component

@Component
class FeedbackCategorySelectMenu : DiscordSelectMenu {
    override val id = "select:feedback:category"

    override fun create() = StringSelectMenu
        .create(id)
        .addOptions(FeedbackCategory.entries.map {
            SelectOption.of(it.name.lowercase().replaceFirstChar { it.uppercase() }, it.name)
        })
        .build()
}