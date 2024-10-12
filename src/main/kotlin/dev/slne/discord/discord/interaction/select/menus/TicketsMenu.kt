package dev.slne.discord.discord.interaction.select.menus

import dev.slne.data.api.DataApi
import dev.slne.discord.discord.interaction.modal.step.creator.report.ReportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.unban.UnbanTicketChannelCreationModal
import dev.slne.discord.discord.interaction.select.DiscordSelectMenu
import dev.slne.discord.spring.feign.dto.WhitelistDTO
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketType
import dev.slne.discord.ticket.result.TicketCreateResult
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.interactions.modals.Modal
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function
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
    override fun onSelect(
        interaction: StringSelectInteraction,
        selectedOptions: List<DiscordSelectMenuOption?>
    ) {
        val option = selectedOptions.first()
        val ticketType: TicketType = TicketType.getByName(option.getLabel())

        interaction.getMessage().delete().queue()

        if (ticketType == TicketType.WHITELIST) {
            handleWhitelist(ticketType, interaction)
            return
        }

        if (ticketType == TicketType.UNBAN) {
            handleUnban(interaction)
            return
        }

        if (ticketType == TicketType.REPORT) { // TODO: 18.08.2024 10:32 - special treatment for ChannelCreationModals
            handleReport(ticketType, interaction)
            return
        }

        interaction.deferReply(true).queue(Consumer<InteractionHook> { hook: InteractionHook ->
            val user: User = interaction.getUser()
            val guild: Guild? = interaction.getGuild()

            val ticketFuture: CompletableFuture<Ticket?> = CompletableFuture()

            if (ticketType == TicketType.DISCORD_SUPPORT) {
                ticketFuture.complete(DiscordSupportTicket(guild, user))
            } else {
                WhitelistDTO.isWhitelisted(user)
                    .thenAcceptAsync(Consumer<Boolean> { whitelisted: Boolean? ->
                        val whitelistedTypes: List<TicketType> = java.util.List.of<TicketType>(
                            TicketType.SURVIVAL_SUPPORT
                        )
                        if (!whitelisted!! && whitelistedTypes.contains(ticketType)) {
                            sendNotWhitelistedMessage(hook)
                            return@thenAcceptAsync
                        }

                        var ticket: Ticket? = null
                        when (ticketType) {
                            TicketType.EVENT_SUPPORT -> ticket = EventSupportTicket(guild, user)
                            TicketType.SURVIVAL_SUPPORT -> ticket = ServerSupportTicket(guild, user)
                            TicketType.BUGREPORT -> ticket = BugreportTicket(guild, user)
                            else -> {}
                        }
                        ticketFuture.complete(ticket)
                    })
            }

            ticketFuture.thenAcceptAsync(Consumer<Ticket?> { ticket: Ticket? ->
                if (ticket == null) {
                    hook.editOriginal(
                        "Es konnte kein Ticket mit dem angegebenen Ticket-Typen erstellt werden!"
                    )
                        .queue()
                    return@thenAcceptAsync
                }
                ticket.openFromButton().thenAcceptAsync({ result ->
                    if (result.equals(TicketCreateResult.SUCCESS)) {
                        val message: StringBuilder = StringBuilder()
                        message.append("Dein \"")
                        message.append(ticketType.getName())
                        message.append("\"-Ticket wurde erfolgreich erstellt! ")

                        if (ticket.thread != null) {
                            message.append(ticket.thread!!.getAsMention())
                        }

                        hook.editOriginal(message.toString()).queue()
                    } else if (result.equals(TicketCreateResult.ALREADY_EXISTS)) {
                        hook.editOriginal(
                            "Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo."
                        )
                            .queue()
                    } else if (result.equals(TicketCreateResult.MISSING_PERMISSIONS)) {
                        hook.editOriginal(
                            "Du hast nicht die benötigten Berechtigungen, um ein Ticket zu erstellen!"
                        )
                            .queue()
                    } else {
                        hook.editOriginal("Es ist ein Fehler aufgetreten!").queue()
                        DataApi.getDataInstance()
                            .logError(
                                javaClass,
                                java.lang.String.format("Error while creating ticket: %s", result)
                            )
                    }
                }).exceptionally({ failure ->
                    hook.editOriginal("Es ist ein Fehler aufgetreten!").queue()
                    DataApi.getDataInstance()
                        .logError(javaClass, "Error while creating ticket", failure)
                    null
                })
            }).exceptionally(Function<Throwable, Void?> { failure: Throwable? ->
                hook.editOriginal("Es ist ein Fehler aufgetreten!").queue()
                DataApi.getDataInstance()
                    .logError(javaClass, "Error while creating ticket", failure)
                null
            })
            interaction.editSelectMenu(interaction.getSelectMenu()).queue()
        })
    }


    /**
     * Handles the whitelist button
     *
     * @param ticketType  the ticket type
     * @param interaction the interaction
     */
    private fun handleWhitelist(ticketType: TicketType, interaction: StringSelectInteraction) {
        if (ticketType != TicketType.WHITELIST) {
            return
        }

        val user: User = interaction.getUser()

        WhitelistDTO.isWhitelisted(user)
            .thenAcceptAsync(Consumer<Boolean> { whitelistedBoolean: Boolean ->
                val whitelisted: Boolean = whitelistedBoolean
                if (whitelisted) {
                    sendAlreadyWhitelistedMessage(interaction)
                    return@thenAcceptAsync
                }

                val whitelistModal: WhitelistTicketModal = WhitelistTicketModal()
                val modal: Modal = whitelistModal.buildModal()
                interaction.replyModal(modal).queue()
                interaction.editSelectMenu(interaction.getSelectMenu()).queue()
            }).exceptionally(Function<Throwable, Void?> { failure: Throwable? ->
                interaction.reply("Es ist ein Fehler aufgetreten!").setEphemeral(true).queue()
                interaction.editSelectMenu(interaction.getSelectMenu()).queue()
                DataApi.getDataInstance()
                    .logError(javaClass, "Error while checking if user is whitelisted", failure)
                null
            })
    }

    /**
     * Handles the unban button
     *
     * @param interaction the interaction
     */
    private fun handleUnban(interaction: StringSelectInteraction) {
        val reportTicketModal: UnbanTicketChannelCreationModal = UnbanTicketChannelCreationModal()

        reportTicketModal.startChannelCreation(interaction)
            .exceptionally(Function<Throwable, Void?> { failure: Throwable? ->
                DataApi.getDataInstance()
                    .logError(javaClass, "Error while creating unban ticket", failure)
                null
            })
    }

    private fun handleReport(ticketType: TicketType, interaction: StringSelectInteraction) {
        val reportTicketModal: ReportTicketChannelCreationModal = ReportTicketChannelCreationModal()

        reportTicketModal.startChannelCreation(interaction)
            .exceptionally(Function<Throwable, Void?> { failure: Throwable? ->
                DataApi.getDataInstance()
                    .logError(javaClass, "Error while creating report ticket", failure)
                null
            })
    }

    /**
     * Sends a message to the user that he is not whitelisted
     *
     * @param hook the hook
     */
    private fun sendNotWhitelistedMessage(hook: InteractionHook) {
        hook.editOriginal(
            "Du befindest dich nicht auf der WhitelistDTO und kannst dieses Ticket nicht öffnen."
        )
            .queue()
    }

    /**
     * Sends a message to the user that he is allready whitelisted
     *
     * @param interaction the interaction
     */
    private fun sendAlreadyWhitelistedMessage(interaction: StringSelectInteraction) {
        interaction.reply(
            "Du befindest dich bereits auf der WhitelistDTO und kannst dieses Ticket nicht öffnen."
        )
            .setEphemeral(true).queue()
    }
}
