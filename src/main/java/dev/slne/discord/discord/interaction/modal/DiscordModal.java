package dev.slne.discord.discord.interaction.modal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.modals.Modal;

public abstract class DiscordModal {

    private @Nonnull String title;
    protected @Nonnull List<ActionComponent> components;

    /**
     * Creates a new DiscordModal.
     *
     * @param title The title of the modal.
     */
    protected DiscordModal(@Nonnull String title) {
        this.title = title;

        this.components = new ArrayList<>();
    }

    /**
     * Fills the components of the modal.
     */
    public abstract void fillComponents();

    /**
     * Executes the modal.
     *
     * @param event The event.
     */
    public abstract void execute(ModalInteractionEvent event);

    /**
     * Returns the custom id of the modal.
     *
     * @return The custom id of the modal.
     */
    public abstract @Nonnull String getCustomId();

    /**
     * Builds the modal.
     *
     * @return The modal.
     */
    public @Nonnull Modal buildModal() {
        Modal.Builder modalBuilder = Modal.create(getCustomId(), this.title);

        fillComponents();

        if (!this.components.isEmpty()) {
            for (ActionComponent component : this.components) {
                modalBuilder.addActionRow(component);
            }
        }

        return modalBuilder.build();
    }

    /**
     * Opens the modal.
     *
     * @param event The event.
     */
    public void open(SlashCommandInteractionEvent event) {
        Modal modal = this.buildModal();
        event.replyModal(modal).queue();
    }

    /**
     * Returns the title of the modal.
     *
     * @return The title of the modal.
     */
    public @Nonnull String getTitle() {
        return title;
    }

    /**
     * Returns the components of the modal.
     *
     * @return The components of the modal.
     */
    public @Nonnull List<ActionComponent> getComponents() {
        return components;
    }

}
