package dev.slne.discord.discord.interaction.modal

import dev.slne.data.api.DataApi
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

/**
 * The type Discord modal manager.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DiscordModalManager {
    private val modals: MutableMap<String?, Class<out DiscordModal>> = ConcurrentHashMap()
    private val advancedModals: Object2ObjectMap<String, Supplier<DiscordStepChannelCreationModal>> =
        Object2ObjectOpenHashMap<String, Supplier<DiscordStepChannelCreationModal>>()
    private val currentUserModals: Object2ObjectMap<String, DiscordStepChannelCreationModal> =
        Object2ObjectOpenHashMap<String, DiscordStepChannelCreationModal>()

    init {
        registerModal(WhitelistTicketModal::class.java)
        //		registerModal(UnbanTicketModal.class);
//		registerAdvancedModal(ReportTicketChannelCreationModal::new);
//		registerAdvancedModal(UnbanTicketChannelCreationModal::new);
    }

    /**
     * Register a modal
     *
     * @param modalClass The class of the modal
     */
    fun registerModal(modalClass: Class<out DiscordModal>) {
        if (modals.containsValue(modalClass)) {
            return
        }

        val discordModal: DiscordModal? = getModalByClass(modalClass)

        if (discordModal == null) {
            return
        }

        val customId: String? = discordModal.getCustomId()
        modals.put(customId, modalClass)
    }

    fun registerAdvancedModal(
        modalId: String,
        creator: Supplier<DiscordStepChannelCreationModal>
    ) {
        advancedModals.putIfAbsent(modalId, creator)
    }

    /**
     * Get the modal by the class
     *
     * @param clazz The class of the modal
     * @return DiscordModal modal by class
     * @throws IllegalArgumentException If the class doesn't have a constructor
     */
    @Throws(IllegalArgumentException::class)
    private fun getModalByClass(clazz: Class<out DiscordModal>?): DiscordModal? {
        var discordModal: DiscordModal? = null

        if (clazz == null) {
            return null
        }

        try {
            discordModal = clazz.getDeclaredConstructors().get(0).newInstance() as DiscordModal
        } catch (exception: InstantiationException) {
            DataApi.getDataInstance()
                .logError(
                    javaClass, "Failed to create a new instance of the modal class.",
                    exception
                )
        } catch (exception: IllegalAccessException) {
            DataApi.getDataInstance()
                .logError(
                    javaClass, "Failed to create a new instance of the modal class.",
                    exception
                )
        } catch (exception: IllegalArgumentException) {
            DataApi.getDataInstance()
                .logError(
                    javaClass, "Failed to create a new instance of the modal class.",
                    exception
                )
        } catch (exception: InvocationTargetException) {
            DataApi.getDataInstance()
                .logError(
                    javaClass, "Failed to create a new instance of the modal class.",
                    exception
                )
        } catch (exception: SecurityException) {
            DataApi.getDataInstance()
                .logError(
                    javaClass, "Failed to create a new instance of the modal class.",
                    exception
                )
        }

        requireNotNull(discordModal) { "The modal class must have a constructor without parameters" }

        return discordModal
    }

    /**
     * Get the modal by the custom id
     *
     * @param customId The custom id of the modal
     * @return DiscordModal modal
     */
    fun getModal(customId: String?): DiscordModal? {
        return getModalByClass(modals.get(customId))
    }

    fun getAdvancedModal(customId: String, userId: String): DiscordStepChannelCreationModal? {
        val modalCreator: Supplier<DiscordStepChannelCreationModal>? = advancedModals.get(customId)

        if (modalCreator == null) {
            return null
        }

        val currentModal: DiscordStepChannelCreationModal? = currentUserModals.get(userId)

        if (currentModal != null) {
            return currentModal
        }

        return modalCreator.get()
    }

    fun setCurrentUserModal(userId: String, modal: DiscordStepChannelCreationModal) {
        currentUserModals.put(userId, modal)
    }

    companion object {
        val INSTANCE: DiscordModalManager = DiscordModalManager()
    }
}
