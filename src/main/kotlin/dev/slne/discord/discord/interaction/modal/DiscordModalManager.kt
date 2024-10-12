package dev.slne.discord.discord.interaction.modal

import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.lang.reflect.InvocationTargetException
import java.util.function.Supplier

object DiscordModalManager {

    private val logger = ComponentLogger.logger(DiscordModalManager::class.java)
    private val modals: MutableMap<String?, Class<DiscordModal>> = mutableMapOf()
    private val advancedModals: Object2ObjectMap<String, Supplier<DiscordStepChannelCreationModal>> =
        Object2ObjectOpenHashMap()

    private val currentUserModals: Object2ObjectMap<String, DiscordStepChannelCreationModal> =
        Object2ObjectOpenHashMap()

    init {
//        registerModal(WhitelistTicketModal::class.java)
        //		registerModal(UnbanTicketModal.class);
//		registerAdvancedModal(ReportTicketChannelCreationModal::new);
//		registerAdvancedModal(UnbanTicketChannelCreationModal::new);
    }

    private fun registerModal(modalClass: Class<DiscordModal>) =
        modals.putIfAbsent(getModalByClass(modalClass).customId, modalClass)

    fun registerAdvancedModal(
        modalId: String,
        creator: () -> DiscordStepChannelCreationModal
    ) = advancedModals.putIfAbsent(modalId, creator)

    private fun getModalByClass(clazz: Class<DiscordModal>): DiscordModal {
        var discordModal: DiscordModal? = null

        try {
            discordModal = clazz.getDeclaredConstructors()[0].newInstance() as DiscordModal
        } catch (exception: InstantiationException) {
            logger.error(
                "Failed to create a new instance of the modal class.",
                exception
            )
        } catch (exception: IllegalAccessException) {
            logger.error(
                "Failed to create a new instance of the modal class.",
                exception
            )
        } catch (exception: IllegalArgumentException) {
            logger.error(
                "Failed to create a new instance of the modal class.",
                exception
            )
        } catch (exception: InvocationTargetException) {
            logger.error(
                "Failed to create a new instance of the modal class.",
                exception
            )
        } catch (exception: SecurityException) {
            logger.error(
                "Failed to create a new instance of the modal class.",
                exception
            )
        }

        requireNotNull(discordModal) { "The modal class must have a constructor without parameters" }

        return discordModal
    }

    fun getModal(customId: String) = modals[customId]?.let {
        getModalByClass(it)
    }

    fun getAdvancedModal(customId: String, userId: String): DiscordStepChannelCreationModal? {
        val modalCreator: Supplier<DiscordStepChannelCreationModal> = advancedModals[customId]
            ?: return null

        val currentModal: DiscordStepChannelCreationModal? = currentUserModals[userId]

        if (currentModal != null) {
            return currentModal
        }

        return modalCreator.get()
    }

    fun setCurrentUserModal(userId: String, modal: DiscordStepChannelCreationModal) {
        currentUserModals[userId] = modal
    }
}
