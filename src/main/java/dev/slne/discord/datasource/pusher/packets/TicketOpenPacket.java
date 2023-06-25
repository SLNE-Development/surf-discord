package dev.slne.discord.datasource.pusher.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.slne.data.core.pusher.packet.PusherPacket;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketRepository;

public class TicketOpenPacket extends PusherPacket {

    private Ticket ticket;

    /**
     * Create a new instance of the TicketOpenPacket class.
     */
    public TicketOpenPacket() {

    }

    /**
     * Create a new instance of the TicketOpenPacket class.
     *
     * @param ticket The ticket that was opened.
     */
    public TicketOpenPacket(Ticket ticket) {
        this.ticket = ticket;
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
        if (jsonObject == null || !jsonObject.has("ticket")) {
            return;
        }

        JsonObject ticketObject = jsonObject.getAsJsonObject("ticket");
        if (ticketObject == null) {
            return;
        }

        ticket = TicketRepository.ticketByJson(ticketObject);
    }

    /**
     * @return the ticket
     */
    public Ticket getTicket() {
        return ticket;
    }

}
