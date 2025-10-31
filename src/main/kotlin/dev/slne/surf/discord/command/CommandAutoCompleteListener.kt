package dev.slne.surf.discord.command

import dev.slne.surf.discord.faq.Faq
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command
import org.springframework.stereotype.Component

@Component
class CommandAutoCompleteListener(
    private val jda: JDA
) : ListenerAdapter() {
    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        if (event.name != "faq" || event.focusedOption.name != "question") return

        val input = event.focusedOption.value.lowercase()
        val suggestions = Faq.entries
            .filter { it.id.lowercase().contains(input) || it.question.lowercase().contains(input) }
            .take(25)
            .map { it.id }

        event.replyChoices(
            suggestions.map { Command.Choice(it, it) }
        ).queue()
    }
}
