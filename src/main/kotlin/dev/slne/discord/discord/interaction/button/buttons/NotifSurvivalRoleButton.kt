package dev.slne.discord.discord.interaction.button.buttons

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discord.annotation.DiscordButton
import dev.slne.discord.annotation.DiscordEmoji
import dev.slne.discord.discord.interaction.button.DiscordButtonHandler
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle

const val NotifySurvivalRoleTicketButtonId = "survival-notify-role"

@DiscordButton(
    NotifySurvivalRoleTicketButtonId,
    "Survival-Server",
    ButtonStyle.SUCCESS,
    DiscordEmoji(unicode="🏕️")
)
class NotifySurvivalRoleButton() : DiscordButtonHandler {

    override suspend fun ButtonInteractionEvent.onClick() {
        val member = this.member ?: return
        val guild = this.guild ?: return
        val roleId = "1052595432913637469"
        val role = guild.getRoleById(roleId) ?: return

        if (member.roles.contains(role)) {
            guild.removeRoleFromMember(member, role).await()

            sendEmbed(true, interaction)
            return
        }
        guild.addRoleToMember(member, role).await()
        return
    }

    private suspend fun sendEmbed(state: Boolean, interaction: ButtonInteraction) {
        val embed = Embed {
            title = translatable("interaction.button.notify-survival.title")
            description = if (state) {
                translatable("interaction.button.notify-survival.description.added")
            } else {
                translatable("interaction.button.notify-survival.description.removed")
            }
            color = EmbedColors.NOTIFY_ROLE
        }

        interaction.deferReply(true)
            .await()
            .sendMessageEmbeds(embed)
            .setEphemeral(true)
            .await()
    }

}