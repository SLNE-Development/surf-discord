package dev.slne.discord.discord.interaction.modal.step

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.InlineModal
import dev.minn.jda.ktx.interactions.components.replyModal
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.ChannelCreationModalManager
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.message.RawMessages
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketChannelHelper
import dev.slne.discord.ticket.result.TicketCreateResult
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.jetbrains.annotations.ApiStatus
import kotlin.reflect.full.findAnnotation

abstract class DiscordStepChannelCreationModal(
    private val title: String
) {
    private val logger = ComponentLogger.logger(javaClass)
    private val steps: List<ModalStep> by lazy { buildSteps().steps }

    private val ticketType = this::class.findAnnotation<ChannelCreationModal>()?.let {
        ChannelCreationModalManager.getTicketType(it)
    } ?: error("Creation modal must be annotated with @ChannelCreationModal")

    @ApiStatus.OverrideOnly
    protected abstract fun buildSteps(): StepBuilder

    private fun InlineModal.buildModalComponents() {
        steps.forEach { it.fillModalComponents(this) }
    }

    suspend fun startChannelCreation(
        interaction: StringSelectInteraction,
        guild: Guild
    ) {
        val hook = interaction.deferReply(true).await()

        if (checkTicketExists(guild, interaction.user)) {
            hook.editOriginal(RawMessages.get("error.ticket.type.already-open")).await()
            return
        }

        DiscordModalManager.setCurrentUserModal(
            interaction.user.id,
            this
        )

        if (!hasSelectionStep()) {
            replyModal(interaction)
            return
        }

        startChannelCreationWithSelectionSteps(interaction, hook)
    }

    private suspend fun startChannelCreationWithSelectionSteps(
        interaction: StringSelectInteraction,
        hook: InteractionHook
    ) = replyModalAfterSelectionSteps(executeSelectionSteps(hook), interaction)

    private suspend fun replyModalAfterSelectionSteps(
        lastSelectionEvent: StringSelectInteractionEvent?,
        interaction: StringSelectInteraction,
    ) {
        val callback = lastSelectionEvent ?: interaction
        lastSelectionEvent?.message?.delete()?.await()

        replyModal(callback)
    }

    private suspend fun replyModal(
        modalCallback: IModalCallback,
    ) = modalCallback.replyModal(id, title) { buildModalComponents() }.await()

    suspend fun handleUserSubmitModal(event: ModalInteractionEvent) {
        val modalSteps = steps
        val user = event.user

        if (!verifyModalInput(event, modalSteps)) {
            return
        }

        if (!preThreadCreation(event, modalSteps)) {
            return
        }

        val ticket = Ticket(event.guild, user, ticketType)
        val result = ticket.openFromButton()

        postThreadCreated(ticket, result, event, user)
    }

    private fun checkTicketExists(guild: Guild, user: User): Boolean =
        TicketChannelHelper.checkTicketExists(guild, ticketType, user)

    private suspend fun executeSelectionSteps(
        hook: InteractionHook
    ) = executeSelectionSteps(hook, steps, null, null)

    private suspend fun executeSelectionSteps(
        hook: InteractionHook,
        steps: List<ModalStep>,
        lastEvent: StringSelectInteractionEvent?,
        lastMessage: Message?
    ): StringSelectInteractionEvent? {
        var lastStepEvent = lastEvent
        var lastStepMessage = lastMessage

        for (step in steps) {
            if (step is ModalSelectionStep) {
                lastStepMessage?.delete()?.await()

                lastStepMessage = hook.sendMessage(MessageCreate {
                    content = step.selectTitle
                    actionRow(step.createSelection())
                }).setEphemeral(true).await()

                lastStepEvent = step.event
            }

            val children = step.children

            if (children.isNotEmpty()) {
                lastStepEvent =
                    executeSelectionSteps(hook, children, lastStepEvent, lastStepMessage)
            }
        }

        return lastStepEvent
    }

    private suspend fun verifyModalInput(
        event: ModalInteractionEvent,
        steps: List<ModalStep>
    ): Boolean {
        for (step in steps) {
            try {
                step.runVerifyModalInput(event)
            } catch (exception: ModalStep.ModalStepInputVerificationException) {
                reply(event, "${exception.message}")
                return false
            }
        }

        return true
    }

    private suspend fun preThreadCreation(
        event: ModalInteractionEvent,
        steps: List<ModalStep>
    ): Boolean {
        for (step: ModalStep in steps) {
            try {
                step.runPreThreadCreation()
            } catch (exception: ModalStep.ModuleStepChannelCreationException) {
                reply(event, "${exception.message}")

                return false
            }
        }

        return true
    }

    private suspend fun postThreadCreated(
        ticket: Ticket,
        result: TicketCreateResult,
        event: ModalInteractionEvent,
        user: User
    ) {
        val thread = ticket.thread

        if (thread == null) {
            reply(event, "Es ist ein Fehler aufgetreten (postThreadCreated@threadNull)!")
            logger.error("Ticket creation failed with result: {}", result)
            return
        }

        when (result) {
            TicketCreateResult.SUCCESS -> handleSuccess(thread, event, user)
            TicketCreateResult.ALREADY_EXISTS -> reply(
                event,
                RawMessages.get("error.ticket.type.already-open")
            )

            TicketCreateResult.MISSING_PERMISSIONS -> reply(
                event,
                RawMessages.get("error.ticket.permission.open")
            )

            else -> {
                reply(event, "Es ist ein Fehler aufgetreten (postThreadCreated@unknownReason)!")
                logger.error("Ticket creation failed with result: {}", result)
            }
        }
    }

    private suspend fun reply(
        callback: IReplyCallback,
        message: String
    ) { // TODO: is this even correct?
        if (callback.isAcknowledged) {
            callback.hook.sendMessage(message).setEphemeral(true).await()
        } else {
            callback.deferReply(true).await().sendMessage(message).await()
        }
    }

    private suspend fun handleSuccess(
        thread: ThreadChannel,
        event: ModalInteractionEvent,
        user: User
    ) {
        val message = buildString {
            append(RawMessages.get("ticket.open.success", ticketType.displayName))
            ticketType
            append(thread.asMention)
        }

        reply(event, message)
        doWithCreatedThread(thread, user)
    }

    private suspend fun doWithCreatedThread(thread: ThreadChannel, user: User) {
        val messages = MessageQueue()

        messages.getOpenMessages(thread, user)

        steps.forEach { it.getOpenMessages(messages, thread) }

        sendOpenMessage(messages.buildMessages(), thread)

        onPostThreadCreated(thread)
        steps.forEach { it.runPostThreadCreated(thread) }
    }

    private suspend fun sendOpenMessage(messages: List<String>, thread: ThreadChannel) {
        if (messages.isEmpty()) {
            return
        }

        thread.sendMessage(messages.first()).await()

        sendOpenMessage(messages.drop(1), thread)
    }

    @ApiStatus.OverrideOnly
    protected open fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        // Override if necessary
    }

    @ApiStatus.OverrideOnly
    protected fun onPostThreadCreated(thread: ThreadChannel) {
        // Override if necessary
    }

    private fun hasSelectionStep() = steps.any { it.hasSelectionStep() }

    private val id = ChannelCreationModalManager.getModalId(
        javaClass.getAnnotation(ChannelCreationModal::class.java)
    )
}
