package dev.slne.discord.discord.interaction.modal.step

import dev.slne.discord.ticket.Ticket
import lombok.Getter
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction
import net.dv8tion.jda.api.interactions.modals.Modal
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.Contract
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

/**
 * Represents a modal for creating a channel step by step in Discord.
 *
 *
 * This abstract class is intended to be extended to define the specific steps required for
 * creating a channel via modals in Discord. It handles the initialization and management of these
 * steps, as well as the interaction flow for users.
 */
@Getter
@Accessors(makeFinal = true)
abstract class DiscordStepChannelCreationModal
/**
 * Constructs a new DiscordStepChannelCreationModal with the specified parameters.
 *
 * @param title The title of the modal.
 */ protected constructor(@field:Nonnull @param:Nonnull private val title: String?) {
    @Getter(lazy = true)
    private val id: String = id0

    @Getter(lazy = true)
    private val ticketType: TicketType = ticketType0

    @Getter(lazy = true)
    private val steps: LinkedList<ModalStep> = buildSteps().getSteps()

    /**
     * Builds the steps that will be used in this modal.
     *
     * @return A StepBuilder object containing the steps.
     * @see StepBuilder.startWith
     */
    @ApiStatus.OverrideOnly
    protected abstract fun buildSteps(): StepBuilder

    private fun buildModalComponents(): ModalComponentBuilder {
        val builder: ModalComponentBuilder = ModalComponentBuilder()
        getSteps().forEach { step -> step.fillModalComponents(builder) }

        return builder
    }

    /**
     * Initiates the channel creation process starting with any selection steps if required.
     *
     * @param interaction The interaction triggering the channel creation.
     * @return A CompletableFuture that will be completed when the channel creation process is done.
     */
    fun startChannelCreation(
        interaction: StringSelectInteraction
    ): CompletableFuture<Void?> {
        val done: CompletableFuture<Void?> = CompletableFuture<Void?>()
            .exceptionally({ throwable: Throwable? ->
                LOGGER.error(
                    "Error while creating ticket",
                    throwable
                )
                null
            })

        interaction.deferReply(true).queue({ hook: InteractionHook ->
            if (checkTicketExists(interaction.guild, interaction.user)) {
                hook.editOriginal(
                    "Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo."
                )
                    .queue()
                done.complete(null)
                return@queue
            }
            DiscordModalManager.Companion.INSTANCE.setCurrentUserModal(
                interaction.user.id,
                this
            )

            if (!hasSelectionStep()) {
                replyModal(interaction, done)
                return@queue
            }
            startChannelCreationWithSelectionSteps(interaction, done, hook)
        })

        return done
    }

    /**
     * Starts the channel creation process, handling any required selection steps.
     *
     * @param interaction The interaction triggering the channel creation.
     * @param done        A CompletableFuture to signal the completion of the process.
     */
    private fun startChannelCreationWithSelectionSteps(
        interaction: StringSelectInteraction,
        done: CompletableFuture<Void?>,
        hook: InteractionHook
    ) {
        executeSelectionSteps(hook)
            .thenAcceptAsync(
                { lastSelectionEvent: StringSelectInteractionEvent? ->
                    replyModalAfterSelectionSteps(
                        lastSelectionEvent, interaction,
                        done
                    )
                })
    }

    /**
     * Replies with the modal after handling the selection steps.
     *
     * @param lastSelectionEvent The last selection event that occurred or null if there was none.
     * @param interaction        The original interaction that started the process.
     * @param done               A CompletableFuture to signal the completion of the process.
     */
    private fun replyModalAfterSelectionSteps(
        lastSelectionEvent: StringSelectInteractionEvent?,
        interaction: StringSelectInteraction,
        done: CompletableFuture<Void?>
    ) {
        val callback: IModalCallback =
            if (lastSelectionEvent != null) lastSelectionEvent else interaction

        if (lastSelectionEvent != null) {
            lastSelectionEvent.message.delete().queue()
        }

        replyModal(callback, done)
    }

    /**
     * Replies to the interaction with the modal.
     *
     * @param modalCallback The callback to reply to.
     * @param callback      A CompletableFuture to signal the completion of the process.
     */
    private fun replyModal(
        modalCallback: IModalCallback,
        callback: CompletableFuture<Void?>
    ) {
        modalCallback.replyModal(buildModal())
            .queue(
                { unused: Void? -> callback.complete(null) },
                { ex: Throwable? -> callback.completeExceptionally(ex) })
    }

    /**
     * Submits the modal input and proceeds with channel creation.
     *
     * @param event The modal interaction event.
     */
    @Blocking
    fun handleUserSubmitModal(event: ModalInteractionEvent) {
        val modalSteps: LinkedList<ModalStep> = getSteps()
        val user: User = event.user

        if (!verifyModalInput(event, modalSteps)) {
            return
        }

        if (!preChannelCreation(event, modalSteps)) {
            return
        }

        val ticket: Ticket = Ticket(event.guild, user, getTicketType())
        ticket.openFromButton()
            .thenAcceptAsync { result -> postChannelCreated(ticket, result, event, user) }
            .exceptionally { e ->
                reply(event, "Es ist ein Fehler aufgetreten! (ophdo9upou76967867)")
                LOGGER.error(
                    "Error while creating ticket",
                    e
                )
                null
            }
    }

    private fun checkTicketExists(guild: Guild?, user: User): Boolean {
        return checkTicketExistsFast(guild, getTicketType(), user)
    }

    /**
     * Builds the modal from the components generated by the steps.
     *
     * @return The built Modal object.
     */
    private fun buildModal(): Modal {
        val builder: Modal.Builder = Modal.create(
            getId(),
            title!!
        )

        for (component: ActionComponent in buildModalComponents().getComponents()) {
            builder.addActionRow(component)
        }

        return builder.build()
    }

    /**
     * Performs the selection steps sequentially, waiting for user interaction at each step.
     *
     * @param hook The interaction hook used to send messages and interact with the user.
     * @return A CompletableFuture that will complete with the last StringSelectInteractionEvent, or
     * null if none occurred.
     */
    @Contract("_ -> new")
    private fun executeSelectionSteps(
        hook: InteractionHook
    ): CompletableFuture<StringSelectInteractionEvent?> {
        return CompletableFuture.supplyAsync<StringSelectInteractionEvent?>(
            { executeSelectionSteps(hook, getSteps(), null, null) })
    }

    /**
     * Performs the selection steps recursively.
     *
     * @param hook        The interaction hook used to send messages and interact with the user.
     * @param steps       The list of steps to process.
     * @param lastEvent   The last StringSelectInteractionEvent, which occurred, or null if none.
     * @param lastMessage The last Message that was sent or null if none.
     * @return The last StringSelectInteractionEvent, which occurred, or null if none.
     */
    private fun executeSelectionSteps(
        hook: InteractionHook,
        steps: LinkedList<ModalStep>,
        lastEvent: StringSelectInteractionEvent?,
        lastMessage: Message?
    ): StringSelectInteractionEvent? {
        var lastEvent: StringSelectInteractionEvent? = lastEvent
        var lastMessage: Message? = lastMessage
        for (step: ModalStep in steps) {
            if (step is ModalSelectionStep) {
                if (lastMessage != null) {
                    lastMessage.delete().queue()
                }

                val message: AtomicReference<Message> = AtomicReference()

                hook.sendMessage(step.getSelectTitle())
                    .setEphemeral(true)
                    .setActionRow(step.createSelection()).queue(
                        Consumer<Message> { newValue: Message -> message.set(newValue) },
                        Consumer<Throwable> { obj: Throwable -> obj.printStackTrace() })

                lastEvent = step.getSelectionFuture().join()
                lastMessage = message.get()
            }

            val children: LinkedList<ModalStep> = step.getChildren()
            if (!children.isEmpty()) {
                lastEvent = executeSelectionSteps(hook, children, lastEvent, lastMessage)
            }
        }

        return lastEvent
    }

    /**
     * Verifies the user input for all steps.
     *
     * @param event The modal interaction event.
     * @param steps The list of steps to verify.
     */
    private fun verifyModalInput(
        event: ModalInteractionEvent,
        steps: LinkedList<ModalStep>
    ): Boolean {
        for (step: ModalStep in steps) {
            try {
                step.runVerifyModalInput(event)
            } catch (e: ModalStepInputVerificationException) {
                reply(event, e.message)
                return false
            }
        }

        return true
    }

    /**
     * Prepares the channel creation process for all steps.
     *
     * @param event The modal interaction event.
     * @param steps The list of steps to process.
     */
    private fun preChannelCreation(
        event: ModalInteractionEvent,
        steps: LinkedList<ModalStep>
    ): Boolean {
        for (step: ModalStep in steps) {
            try {
                step.runPreChannelCreationAsync()
            } catch (e: ModuleStepChannelCreationException) {
                reply(event, e.message)
                return false
            }
        }

        return true
    }

    /**
     * Handles the result of the channel creation process.
     *
     * @param ticket The ticket that was created.
     * @param result The result of the ticket creation.
     * @param event  The modal interaction event.
     * @param user   The user who initiated the channel creation.
     */
    private fun postChannelCreated(
        ticket: Ticket,
        result: TicketCreateResult,
        event: ModalInteractionEvent,
        user: User
    ) {
        val channel: TextChannel? = ticket.channel

        when (result) {
            TicketCreateResult.SUCCESS -> handleSuccess(channel, event, user)
            TicketCreateResult.ALREADY_EXISTS -> reply(
                event,
                "Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo."
            )

            TicketCreateResult.MISSING_PERMISSIONS -> reply(
                event,
                "Du hast nicht die benötigten Berechtigungen, um ein Ticket zu erstellen!"
            )

            else -> {
                reply(event, "Es ist ein Fehler aufgetreten (qopuewuopfbop8729)!")
                LOGGER.error("Ticket creation failed with result: {}", result)
            }
        }
    }

    /**
     * Replies to the user with a specified message.
     *
     * @param callback The callback to reply to.
     * @param message  The message to send.
     */
    private fun reply(callback: IReplyCallback, message: String) {
        if (callback.isAcknowledged) {
            callback.hook.sendMessage(message).setEphemeral(true).queue()
        } else {
            callback.deferReply(true)
                .queue({ hook: InteractionHook -> hook.sendMessage(message).queue() })
        }
    }

    /**
     * Handles successful channel creation, sending a confirmation message to the user and performing
     * any additional setup.
     *
     * @param channel The channel that was created.
     * @param event   The modal interaction event.
     * @param user    The user who initiated the channel creation.
     */
    private fun handleSuccess(channel: TextChannel?, event: ModalInteractionEvent, user: User) {
        val message: StringBuilder = StringBuilder()
        message.append("Dein \"")
        message.append(getTicketType().getName())
        message.append("\"-Ticket wurde erfolgreich erstellt! ")

        if (channel != null) {
            message.append(channel.asMention)
        }

        reply(event, message.toString())

        if (channel != null) {
            doWithCreatedChannel(channel, user)
        }
    }

    /**
     * Performs actions on the newly created channel, such as sending initial messages.
     *
     * @param channel The channel that was created.
     * @param user    The user who initiated the channel creation.
     */
    private fun doWithCreatedChannel(channel: TextChannel, user: User) {
        val messages: MessageQueue = MessageQueue()

        getOpenMessages(messages, channel, user)
        for (step: ModalStep in getSteps()) {
            step.getOpenMessages(messages, channel)
        }

        val message: LinkedList<String?> = messages.buildMessages()

        sendOpenMessage(message, channel)

        onPostChannelCreated(channel)
        getSteps().forEach { step -> step.runPostChannelCreated(channel) }
    }

    /**
     * Sends the queued messages to the specified channel in order.
     *
     * @param messages The list of messages to send.
     * @param channel  The channel to send the messages to.
     */
    private fun sendOpenMessage(messages: LinkedList<String?>, channel: TextChannel) {
        if (messages.isEmpty()) {
            return
        }

        channel.sendMessage(messages.first!!)
            .queue({ message: Message? ->  // ensure messages are sent in order
                messages.removeFirst()
                sendOpenMessage(messages, channel)
            })
    }

    /**
     * Provides open messages related to the created channel.
     *
     * @param messages The message queue to which open messages are added.
     * @param channel  The channel where the messages will be sent.
     * @param user     The user associated with the channel creation.
     */
    @ApiStatus.OverrideOnly
    protected open fun getOpenMessages(messages: MessageQueue, channel: TextChannel?, user: User) {
        // Override if necessary
    }

    /**
     * Executes custom logic after the channel has been created.
     *
     * @param channel The channel that was created.
     */
    @ApiStatus.OverrideOnly
    protected fun onPostChannelCreated(channel: TextChannel?) {
        // Override if necessary
    }

    /**
     * Determines if any of the steps involve a selection process.
     *
     * @return true if any step has a selection process, false otherwise.
     */
    fun hasSelectionStep(): Boolean {
        return getSteps().stream().anyMatch { obj: ModalStep -> obj.hasSelectionStep() }
    }

    private val id0: String
        get() = ChannelCreationModalProcessor.getModalId(
            Objects.requireNonNull<T>(
                AnnotationUtils.findAnnotation(javaClass, ChannelCreationModal::class.java)
            )
        )

    private val ticketType0: TicketType
        get() {
            return ChannelCreationModalProcessor.getTicketType(
                Objects.requireNonNull<T>(
                    AnnotationUtils.findAnnotation(javaClass, ChannelCreationModal::class.java)
                )
            )
        }

    companion object {
        private val LOGGER: ComponentLogger = ComponentLogger.logger(
            "DiscordStepChannelCreationModal"
        )
    }
}
