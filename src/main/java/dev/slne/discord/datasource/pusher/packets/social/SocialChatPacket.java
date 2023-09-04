package dev.slne.discord.datasource.pusher.packets.social;

import com.google.gson.annotations.SerializedName;
import dev.slne.data.api.pusher.packet.PusherPacket;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class SocialChatPacket extends PusherPacket {

    @SerializedName("senderUuid")
    private UUID senderUuid;

    @SerializedName("senderDisplayName")
    private Component senderDisplayName;

    @SerializedName("message")
    private Component message;

    /**
     * Create a new instance of the SocialChatPacket class.
     */
    public SocialChatPacket() {
    }

    /**
     * A packet to send a message to a player.
     *
     * @param senderUuid        The UUID of the sender.
     * @param senderDisplayName The display name of the sender.
     * @param message           The message to be sent to the player.
     */
    public SocialChatPacket(UUID senderUuid, Component senderDisplayName, Component message) {
        this.senderUuid = senderUuid;
        this.senderDisplayName = senderDisplayName;
        this.message = message;
    }

    /**
     * The UUID of the sender.
     *
     * @return The UUID of the sender.
     */
    public UUID getSenderUuid() {
        return senderUuid;
    }

    /**
     * The display name of the sender.
     *
     * @return The display name of the sender.
     */
    public Component getSenderDisplayName() {
        return senderDisplayName;
    }

    /**
     * The message to be sent to the player.
     *
     * @return The message to be sent to the player.
     */
    public Component getMessage() {
        return message;
    }
}
