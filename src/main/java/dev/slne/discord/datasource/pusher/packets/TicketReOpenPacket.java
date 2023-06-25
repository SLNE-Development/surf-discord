package dev.slne.discord.datasource.pusher.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.slne.data.core.pusher.packet.PusherPacket;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketRepository;

public class TicketReOpenPacket extends PusherPacket {

    private Ticket originalTicket;
    private Ticket newTicket;

    /**
     * Create a new instance of the TicketReOpenPacket class.
     */
    public TicketReOpenPacket() {

    }

    /**
     * Create a new instance of the TicketReOpenPacket class.
     *
     * @param originalTicket The original ticket.
     * @param newTicket      The new ticket.
     */
    public TicketReOpenPacket(Ticket originalTicket, Ticket newTicket) {
        this.originalTicket = originalTicket;
        this.newTicket = newTicket;
    }

    @Override
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement == null) {
            return;
        }

        if (!jsonElement.isJsonObject()) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject == null || !jsonObject.has("original_ticket") || !jsonObject.has("new_ticket")) {
            return;
        }

        JsonObject originalTicketObject = jsonObject.getAsJsonObject("original_ticket");
        if (originalTicketObject == null) {
            return;
        }

        JsonObject newTicketObject = jsonObject.getAsJsonObject("new_ticket");
        if (newTicketObject == null) {
            return;
        }

        originalTicket = TicketRepository.ticketByJson(originalTicketObject);
        newTicket = TicketRepository.ticketByJson(newTicketObject);

        if (newTicket != null) {
            DiscordBot.getInstance().getTicketManager().addTicket(newTicket);
        }
    }

    /**
     * @return the newTicket
     */
    public Ticket getNewTicket() {
        return newTicket;
    }

    /**
     * @return the originalTicket
     */
    public Ticket getOriginalTicket() {
        return originalTicket;
    }

}
