package dev.slne.discord.listener.pusher.social;

import dev.slne.data.api.event.Subscribe;
import dev.slne.discord.datasource.pusher.packets.social.SocialChatPacket;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@SuppressWarnings("unused")
public class SocialChatListener {

    @Subscribe
    public void onEvent(SocialChatPacket packet) {
        UUID sender = packet.getSenderUuid(); // Null due to twisti?
        Component senderDisplayName = packet.getSenderDisplayName(); // Null due to twisti?
        Component message = packet.getMessage(); // TODO: 2021-10-10 10:52:00
    }
}
