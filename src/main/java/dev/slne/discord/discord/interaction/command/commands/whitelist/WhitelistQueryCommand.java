package dev.slne.discord.discord.interaction.command.commands.whitelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.whitelist.UUIDResolver;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class WhitelistQueryCommand extends DiscordCommand {

    /**
     * Creates a new {@link WhitelistQueryCommand}.
     */
    public WhitelistQueryCommand() {
        super("wlquery", "Zeigt Whitelist Informationen über einen Benutzer an.");
    }

    @Override
    public @Nonnull List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();

        options.add(new OptionData(OptionType.USER, "user",
                "Der Benutzer über den Informationen angezeigt werden sollen.", false, false));

        options.add(new OptionData(OptionType.STRING, "minecraft",
                "Der Minecraft Name des Benutzers.", false, false));

        options.add(new OptionData(OptionType.STRING, "twitch",
                "Der Twitch Name des Benutzers.", false, false));

        return options;
    }

    @Override
    public @Nonnull DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_WHITELIST_QUERY;
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        if (!(interaction.getChannel() instanceof TextChannel)) {
            interaction.reply("Dieser Befehl kann nur in Textkanälen verwendet werden.").setEphemeral(true).queue();
            return;
        }

        TextChannel channel = (TextChannel) interaction.getChannel();

        OptionMapping userOption = interaction.getOption("user");
        OptionMapping minecraftOption = interaction.getOption("minecraft");
        OptionMapping twitchOption = interaction.getOption("twitch");

        interaction.deferReply().queue(hook -> {
            if (userOption != null && userOption.getAsUser() != null) {
                User user = userOption.getAsUser();

                Whitelist.getWhitelists(null, user.getId(), null).whenComplete(whitelists -> {
                    printWlQuery(channel, "\"" + user.getName() + "\"", whitelists);
                    hook.deleteOriginal().queue();
                }, throwable -> errorHandler(hook, throwable));

                return;
            }

            if (twitchOption != null && twitchOption.getAsString() != null) {
                String twitch = twitchOption.getAsString();

                Whitelist.getWhitelists(null, null, twitch).whenComplete(whitelists -> {
                    printWlQuery(channel, "\"" + twitch + "\"", whitelists);
                    hook.deleteOriginal().queue();
                }, throwable -> errorHandler(hook, throwable));

                return;
            }

            if (minecraftOption != null && minecraftOption.getAsString() != null) {
                String minecraft = minecraftOption.getAsString();

                UUIDResolver.resolve(minecraft).whenComplete(uuidMinecraftNameOptional -> {
                    if (uuidMinecraftNameOptional.isPresent()) {
                        UUID uuid = uuidMinecraftNameOptional.get().uuid();

                        Whitelist.getWhitelists(uuid, null, null).whenComplete(whitelists -> {
                            printWlQuery(channel, "\"" + minecraft + " (" + uuid.toString() + ")\"", whitelists);
                            hook.deleteOriginal().queue();
                        }, throwable -> errorHandler(hook, throwable));
                    } else {
                        hook.editOriginal("Der Minecraft Name \"" + minecraft + "\" konnte nicht aufgelöst werden.")
                                .queue();
                    }
                });

                return;
            }

            hook.editOriginal("Es wurde kein Benutzer angegeben.").queue();
        });
    }

    /**
     * Handles an error.
     *
     * @param hook      The hook.
     * @param throwable The throwable.
     */
    public void errorHandler(InteractionHook hook, Throwable throwable) {
        hook.editOriginal("Es ist ein Fehler aufgetreten.").queue();
        throwable.printStackTrace();
    }

    /**
     * Prints a wlquery request.
     *
     * @param channel    The channel.
     * @param title      The title.
     * @param whitelists The whitelists.
     */
    public void printWlQuery(TextChannel channel, String title, Optional<List<Whitelist>> whitelistsOptional) {
        channel.sendMessage("WlQuery für: \"" + title + "\"");

        if (whitelistsOptional.isPresent()) {
            List<Whitelist> whitelists = whitelistsOptional.get();

            for (Whitelist whitelist : whitelists) {
                channel.sendMessageEmbeds(Whitelist.getWhitelistQueryEmbed(whitelist)).queue();
            }

            if (whitelists.isEmpty()) {
                channel.sendMessage("Es wurden keine Whitelist Einträge gefunden.").queue();
            }

        } else {
            channel.sendMessage("Es wurden keine Whitelist Einträge gefunden.").queue();
        }
    }
}
