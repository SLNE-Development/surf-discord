package dev.slne.discord.interaction.modal;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.modals.Modal;

public abstract class DiscordModal {

    private String title;

    protected List<ActionComponent> components;

    public DiscordModal() {

    }

    public DiscordModal(String title) {
        this.title = title;

        this.components = new ArrayList<>();
    }

    public abstract void fillComponents();

    public abstract void execute(ModalInteractionEvent event);

    public abstract String getCustomId();

    public Modal buildModal() {
        Modal.Builder modalBuilder = Modal.create(getCustomId(), this.title);

        fillComponents();

        if (!this.components.isEmpty()) {
            for (ActionComponent component : this.components) {
                modalBuilder.addActionRow(component);
            }
        }

        return modalBuilder.build();
    }

    public void open(SlashCommandInteractionEvent event) {
        Modal modal = this.buildModal();
        event.replyModal(modal).queue();
    }

    public String getTitle() {
        return title;
    }

    public List<ActionComponent> getComponents() {
        return components;
    }

}
