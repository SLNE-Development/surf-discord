package dev.slne.discord.datasource.pusher.event;

import dev.slne.data.api.pusher.packet.PusherPacket;
import dev.slne.data.api.pusher.packet.event.PusherPacketEvent;
import dev.slne.discord.listener.event.Event;

import java.time.LocalDateTime;

public class DiscordPusherEvent extends Event implements PusherPacketEvent {

    private final PusherPacket packet;
    private final LocalDateTime timestamp;
    private final String channelName;
    private final String pusherEventName;
    private final String userId;

    /**
     * Constructor for the DiscordPusherEvent
     *
     * @param packet          The packet
     * @param timestamp       The timestamp
     * @param channelName     The channel name
     * @param pusherEventName The pusher event name
     * @param userId          The user id
     * @param async           Whether the event is async
     */
    public DiscordPusherEvent(PusherPacket packet, LocalDateTime timestamp, String channelName, String pusherEventName,
                              String userId, boolean async) {
        super(async);

        this.packet = packet;
        this.timestamp = timestamp;
        this.channelName = channelName;
        this.pusherEventName = pusherEventName;
        this.userId = userId;
    }

    @Override
    public PusherPacket packet() {
        return packet;
    }

    @Override
    public LocalDateTime timestamp() {
        return timestamp;
    }

    @Override
    public String channelName() {
        return channelName;
    }

    @Override
    public String pusherEventName() {
        return pusherEventName;
    }

    @Override
    public String userId() {
        return userId;
    }
}
