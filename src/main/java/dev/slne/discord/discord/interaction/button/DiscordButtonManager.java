package dev.slne.discord.discord.interaction.button;

import dev.slne.discord.discord.interaction.button.buttons.ticket.TicketButton.BugreportTicketButton;
import dev.slne.discord.discord.interaction.button.buttons.ticket.TicketButton.DiscordSupportTicketButton;
import dev.slne.discord.discord.interaction.button.buttons.ticket.TicketButton.ServerSupportTicketButton;
import dev.slne.discord.discord.interaction.button.buttons.ticket.TicketButton.WhitelistTicketButton;

import java.util.ArrayList;
import java.util.List;

public class DiscordButtonManager {

    private final List<DiscordButton> buttons;

    /**
     * The DiscordButtonManager
     */
    public DiscordButtonManager() {
        this.buttons = new ArrayList<>();

        addButton(new WhitelistTicketButton());
        addButton(new ServerSupportTicketButton());
        addButton(new DiscordSupportTicketButton());
        addButton(new BugreportTicketButton());
    }

    /**
     * Adds a button
     *
     * @param button the button
     */
    public void addButton(DiscordButton button) {
        buttons.add(button);
    }

    /**
     * Gets a button by its id
     *
     * @param id the id of the button
     *
     * @return the button
     */
    public DiscordButton getButton(String id) {
        return buttons.stream().filter(button -> button.getId().equals(id)).findFirst().orElse(null);
    }

}
