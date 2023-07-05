package dev.slne.discord.listener.whitelist;

import javax.annotation.Nonnull;

import dev.slne.data.core.instance.DataApi;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class WhitelistQuitListener extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        User user = event.getUser();
        Whitelist.getWhitelistByDiscordId(user.getId()).whenComplete(whitelist -> {
            if (whitelist == null) {
                return;
            }

            whitelist.setBlocked(true);

            whitelist.update().whenComplete(whitelistUpdate -> {
                if (whitelistUpdate == null) {
                    DataApi.getDataInstance().logError(getClass(),
                            "Failed to update whitelist for user " + user.getName() + ".");
                    return;
                }

                DataApi.getDataInstance().logInfo(getClass(),
                        "User " + user.getName() + " left the server and was blocked.");
            });
        });
    }

}
