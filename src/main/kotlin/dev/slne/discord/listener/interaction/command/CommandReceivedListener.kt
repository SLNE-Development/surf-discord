package dev.slne.discord.listener.interaction.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import java.util.function.Consumer

/**
 * The type Command received listener.
 */
@DiscordListener
class CommandReceivedListener @Autowired constructor(discordCommandProcessor: DiscordCommandProcessor) :
    ListenerAdapter() {
    private val discordCommandProcessor: DiscordCommandProcessor

    /**
     * Instantiates a new Command received listener.
     */
    init {
        this.discordCommandProcessor = discordCommandProcessor
    }

    override fun onSlashCommandInteraction(@Nonnull event: SlashCommandInteractionEvent) {
        discordCommandProcessor.getCommand(event.getName())
            .ifPresent(Consumer<DiscordCommandHolder> { holder: DiscordCommandHolder ->
                holder.command().execute(event)
            })
    }
}
