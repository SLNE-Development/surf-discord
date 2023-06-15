package dev.slne.discord.listener.setup;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        Launcher.getLogger().logInfo("Bot is ready!");

        // Register commands to guilds
        JDA jda = event.getJDA();

        for (Guild guild : jda.getGuilds()) {
            DiscordBot.getInstance().getCommandManager().registerToGuild(guild);
        }

        DiscordBot.getInstance().getTicketManager().fetchActiveTickets();
    }

}
