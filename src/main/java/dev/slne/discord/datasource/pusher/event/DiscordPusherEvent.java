package dev.slne.discord.datasource.pusher.event;

import java.time.LocalDateTime;

import dev.slne.data.bukkit.pusher.event.BukkitPusherPacketEvent;
import dev.slne.data.core.pusher.event.PusherPacketEvent;
import dev.slne.data.core.pusher.packet.PusherPacket;
import dev.slne.discord.datasource.Times;
import dev.slne.discord.listener.event.Event;

public class DiscordPusherEvent<T extends PusherPacket> extends Event implements PusherPacketEvent<T> {

    private String channelName;
    private String pusherEventName;
    private String userId;

    private T packet;
    private LocalDateTime timestamp;

    /**
     * Creates a new {@link DiscordPusherEvent}
     *
     * <p>
     * Calls
     * {@link DiscordPusherEvent#BukkitPusherPacketEvent(String, String, String, PusherPacket, boolean)}
     * with { @code true} as the last parameter
     * </p
     *
     * @param channelName the name of the channel the packet was received on
     * @param eventName   the name of the event the packet was received on
     * @param userId      the id of the user who sent the packet
     * @param packet      the packet
     *
     */
    public DiscordPusherEvent(String channelName, String eventName, String userId, T packet) {
        this(channelName, eventName, userId, packet, true);
    }

    /**
     * Creates a new {@link BukkitPusherPacketEvent}
     *
     * @param channelName     the name of the channel the packet was received on
     * @param pusherEventName the name of the event the packet was received on
     * @param userId          the id of the user who sent the packet
     * @param packet          the packet
     * @param async           whether the event should be called asynchronously
     */
    public DiscordPusherEvent(String channelName, String pusherEventName, String userId, T packet,
            boolean async) {
        super(async);

        this.packet = packet;
        this.channelName = channelName;
        this.pusherEventName = pusherEventName;
        this.userId = userId;
        this.timestamp = Times.now();
    }

    @Override
    public T getPacket() {
        return this.packet;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getChannelName() {
        return this.channelName;
    }

    @Override
    public String getPusherEventName() {
        return this.pusherEventName;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }
}
