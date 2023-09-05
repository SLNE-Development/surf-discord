package dev.slne.discord.listener.pusher.social;

import dev.slne.discord.datasource.pusher.event.DiscordPusherEvent;
import dev.slne.discord.datasource.pusher.packets.social.SocialChatPacket;
import dev.slne.discord.listener.Listener;
import dev.slne.discord.listener.event.EventHandler;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@SuppressWarnings("unused")
public class SocialChatListener implements Listener {

    @EventHandler
    public void onEvent(DiscordPusherEvent event) {
        if (!(event.packet() instanceof SocialChatPacket packet)) {
            return;
        }

        UUID sender = packet.getSenderUuid(); // Null due to twisti?
        Component senderDisplayName = packet.getSenderDisplayName(); // Null due to twisti?
        Component message = packet.getMessage(); // TODO: 2021-10-10 10:52:00
    }
}
