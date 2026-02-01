package dev.slne.surf.discord.interaction.modal.impl.ticket.whitelist

import dev.slne.surf.discord.interaction.modal.DiscordModal
import org.springframework.stereotype.Component

@Component
class SurvivalWhitelistCreateModal : DiscordModal {
    override val id = "whitelist:modal:create-survival"
}