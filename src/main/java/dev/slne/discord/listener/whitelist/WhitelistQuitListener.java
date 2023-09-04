package dev.slne.discord.listener.whitelist;

import dev.slne.data.api.DataApi;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class WhitelistQuitListener extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        User user = event.getUser();
        Whitelist.getWhitelistByDiscordId(user.getId()).thenAcceptAsync(whitelist -> {
            if (whitelist == null) {
                return;
            }

            whitelist.setBlocked(true);

            whitelist.update().thenAcceptAsync(whitelistUpdate -> {
                if (whitelistUpdate == null) {
                    DataApi.getDataInstance().logError(getClass(),
                            "Failed to update whitelist for user " + user.getName() + ".");
                    return;
                }

                DataApi.getDataInstance().logInfo(getClass(),
                        "User " + user.getName() + " left the server and was blocked.");
            }).exceptionally(throwable -> {
                DataApi.getDataInstance().logError(getClass(),
                        "Failed to update whitelist for user " + user.getName() + ".", throwable);
                return null;
            });
        }).exceptionally(throwable -> {
            DataApi.getDataInstance().logError(getClass(),
                    "Failed to get whitelist for user " + user.getName() + ".", throwable);
            return null;
        });
    }

}
