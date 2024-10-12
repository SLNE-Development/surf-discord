package dev.slne.discord.discord.interaction.select.menus

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.discord.interaction.modal.step.creator.report.ReportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.unban.UnbanTicketChannelCreationModal
import dev.slne.discord.discord.interaction.select.DiscordSelectMenu
import dev.slne.discord.spring.service.whitelist.WhitelistService
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketType
import dev.slne.discord.ticket.getTicketTypeByConfigName
import dev.slne.discord.ticket.result.TicketCreateResult
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import kotlin.math.min

class TicketsMenu(idSuffix: String) : DiscordSelectMenu(
    "menu:tickets-$idSuffix",
    "Ticket auswählen",
    TicketType.entries.map { ticketType: TicketType ->
        DiscordSelectMenuOption(
            ticketType.name,
            ticketType.configName,
            ticketType.description.substring(
                0,
                min(ticketType.description.length, 100)
            ),
            ticketType.emoji
        )
    },
    1,
    1
) {
    private val logger = ComponentLogger.logger(TicketsMenu::class.java)

    override suspend fun onSelect(
        interaction: StringSelectInteraction,
        selectedOptions: List<DiscordSelectMenuOption>
    ) {
        val ticketType = getTicketTypeByConfigName(selectedOptions.first().value)

        interaction.message.delete().await()

        if (ticketType == TicketType.WHITELIST) {
            handleWhitelist(ticketType, interaction)
            return
        }

        if (ticketType == TicketType.UNBAN) {
            handleUnban(interaction)
            return
        }

        if (ticketType == TicketType.REPORT) {
            handleReport(interaction)
            return
        }

        val hook = interaction.deferReply(true).await()
        val user = interaction.user
        val guild = interaction.guild

        var ticket: Ticket? = null

        if (ticketType == TicketType.DISCORD_SUPPORT) {
//            ticket = DiscordSupportTicket(guild, user)
            // FIXME: 12.10.2024 10:05
        } else {
            val whitelisted = WhitelistService.isWhitelisted(user)
            val whitelistedTypes = listOf(
                TicketType.SURVIVAL_SUPPORT
            )
            if (!whitelisted && whitelistedTypes.contains(ticketType)) {
                sendNotWhitelistedMessage(hook)
                return
            }

            when (ticketType) {
//                TicketType.EVENT_SUPPORT -> ticket = EventSupportTicket(guild, user)
//                TicketType.SURVIVAL_SUPPORT -> ticket = ServerSupportTicket(guild, user)
//                TicketType.BUGREPORT -> ticket = BugreportTicket(guild, user)
                // FIXME: 12.10.2024 10:07
                else -> {}
            }
        }

        if (ticket == null) {
            hook.editOriginal(
                "Es konnte kein Ticket mit dem angegebenen Ticket-Typen erstellt werden!"
            ).await()
            return
        }

        when (val result = ticket.openFromButton()) {
            TicketCreateResult.SUCCESS -> {
                val message = buildString {
                    append("Dein \"")
                    append(ticketType?.name ?: "Ticket")
                    append("\"-Ticket wurde erfolgreich erstellt! ")

                    ticket.thread?.let { append(it.asMention) }

                    // FIXME: 12.10.2024 10:09 Shouldnt this be in the handleSuccess handler?
                }

                hook.editOriginal(message).queue()
            }

            TicketCreateResult.ALREADY_EXISTS -> {
                hook.editOriginal(
                    "Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo."
                ).await()
            }

            TicketCreateResult.MISSING_PERMISSIONS -> {
                hook.editOriginal(
                    "Du hast nicht die benötigten Berechtigungen, um ein Ticket zu erstellen!"
                ).await()
            }

            else -> {
                hook.editOriginal("Es ist ein Fehler aufgetreten!").await()
                logger.error("Error while creating ticket: $result")
            }
        }

        interaction.editSelectMenu(interaction.selectMenu).await()
    }

    private suspend fun handleWhitelist(
        ticketType: TicketType,
        interaction: StringSelectInteraction
    ) {
        if (ticketType != TicketType.WHITELIST) {
            return
        }

        val user = interaction.user
        val whitelisted = WhitelistService.isWhitelisted(user)

        if (whitelisted) {
            sendAlreadyWhitelistedMessage(interaction)
            return
        }

//        val whitelistModal = WhitelistTicketModal()
//        val modal: Modal = whitelistModal.buildModal()
//        interaction.replyModal(modal).queue()
        // FIXME: 12.10.2024 10:13
        interaction.editSelectMenu(interaction.selectMenu).await()

    }

    private suspend fun handleUnban(interaction: StringSelectInteraction) =
        UnbanTicketChannelCreationModal().startChannelCreation(interaction)

    private suspend fun handleReport(interaction: StringSelectInteraction) =
        ReportTicketChannelCreationModal().startChannelCreation(interaction)

    private suspend fun sendNotWhitelistedMessage(hook: InteractionHook) =
        hook.editOriginal(
            "Du befindest dich nicht auf der WhitelistDTO und kannst dieses Ticket nicht öffnen."
        ).await()


    private suspend fun sendAlreadyWhitelistedMessage(interaction: StringSelectInteraction) =
        interaction.reply(
            "Du befindest dich bereits auf der WhitelistDTO und kannst dieses Ticket nicht öffnen."
        ).setEphemeral(true).await()

}
