package dev.slne.surf.discord.listener

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.discordScope
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.util.Colors
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.container.ContainerChildComponent
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

private val LINK_COMMAND_REGEX = Regex("""^/link(?: +[\w-]+)?$""", RegexOption.IGNORE_CASE)

object LinkCommandListener : ListenerAdapter() {
    private fun linkComponent(userMention: String): Container {
        val components = mutableListOf<ContainerChildComponent>()

        components += TextDisplay.of("## ${translatable("link.wrong-place.title")}")
        components += TextDisplay.of(translatable("link.wrong-place.description"))
        components += TextDisplay.of("-# $userMention")

        return Container.of(components).withAccentColor(Colors.INFO)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.isWebhookMessage) return
        if (!event.isFromGuild || event.channel is ThreadChannel) return
        if (!LINK_COMMAND_REGEX.matches(event.message.contentRaw.trim())) return

        discordScope.launch {
            event.message.replyComponents(linkComponent(event.author.asMention))
                .useComponentsV2()
                .mention(event.author)
                .await()
        }
    }
}
