package dev.slne.discord.discord.interaction.command.commands.whitelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import dev.slne.data.core.instance.DataApi;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketRepository;
import dev.slne.discord.whitelist.UUIDResolver;
import dev.slne.discord.whitelist.UuidMinecraftName;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class WhitelistCommand extends DiscordCommand {

    /**
     * Creates a new WhitelistCommand.
     */
    public WhitelistCommand() {
        super("whitelist", "Füge einen Spieler zur Whitelist hinzu.");
    }

    @Override
    public @Nonnull List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();

        options.add(new OptionData(OptionType.USER, "user", "Der Spieler, der zur Whitelist hinzugefügt werden soll.")
                .setRequired(true));

        options.add(new OptionData(OptionType.STRING, "minecraft", "Der Minecraft Name des Spielers.")
                .setRequired(true));

        options.add(new OptionData(OptionType.STRING, "twitch", "Der Twitch Name des Spielers.")
                .setRequired(true));

        return options;
    }

    @Override
    public @Nonnull DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_WHITELIST;
    }

    @Override
    @SuppressWarnings("java:S3776")
    public void execute(SlashCommandInteractionEvent interaction) {
        if (!(interaction.getChannel() instanceof TextChannel)) {
            interaction.reply("Dieser Befehl kann nur in einem Ticket verwendet werden.").setEphemeral(true).queue();
            return;
        }

        interaction.deferReply(true).queue(hook -> {
            TextChannel channel = (TextChannel) interaction.getChannel();
            Optional<Ticket> ticketOptional = TicketRepository.getTicketByChannel(channel.getId());

            if (!ticketOptional.isPresent()) {
                hook.editOriginal("Dieser Befehl kann nur in einem Ticket verwendet werden.")
                        .queue();
                return;
            }

            OptionMapping userOption = interaction.getOption("user");
            OptionMapping minecraftOption = interaction.getOption("minecraft");
            OptionMapping twitchOption = interaction.getOption("twitch");

            if (userOption == null) {
                hook.editOriginal("Du musst einen Nutzer angeben.").queue();
                return;
            }

            if (minecraftOption == null) {
                hook.editOriginal("Du musst einen Minecraft Namen angeben.").queue();
                return;
            }

            if (twitchOption == null) {
                hook.editOriginal("Du musst einen Twitch Namen angeben.").queue();
                return;
            }

            User user = userOption.getAsUser();
            String minecraft = minecraftOption.getAsString();
            String twitch = twitchOption.getAsString();
            String discordId = user.getId();

            if (minecraft.length() > 16) {
                hook.editOriginal("Der Minecraft Name darf nicht länger als 16 Zeichen sein.").queue();
                return;
            }

            UUIDResolver.resolve(minecraft).whenComplete(uuidMinecraftNameOptional -> {
                if (uuidMinecraftNameOptional.isEmpty()) {
                    hook.deleteOriginal().queue();
                    channel.sendMessage("Der Minecraft Name ist ungültig.").queue();
                    return;
                }

                UuidMinecraftName uuidMinecraftName = uuidMinecraftNameOptional.get();
                UUID uuid = uuidMinecraftName.uuid();
                String minecraftName = uuidMinecraftName.minecraftName();

                if (!minecraftName.equalsIgnoreCase(minecraft)) {
                    hook.deleteOriginal().queue();
                    channel.sendMessage("Der Minecraft Name ist ungültig.").queue();
                    return;
                }

                Whitelist.getWhitelists(uuid, discordId, twitch).whenComplete(whitelistsOptional -> {
                    if (whitelistsOptional.isPresent() && !whitelistsOptional.get().isEmpty()) {
                        hook.deleteOriginal().queue();
                        channel.sendMessage("Der Spieler befindet sich bereits auf der Whitelist.").queue();

                        for (Whitelist whitelist : whitelistsOptional.get()) {
                            Whitelist.getWhitelistQueryEmbed(whitelist).whenComplete(embed -> {
                                if (embed != null) {
                                    channel.sendMessageEmbeds(embed).queue();
                                }
                            });
                        }

                        return;
                    }

                    Whitelist whitelist = new Whitelist(uuid, minecraftName, twitch, user, interaction.getUser());

                    whitelist.create().whenComplete(whitelistCreateOptional -> {
                        if (whitelistCreateOptional.isEmpty()) {
                            hook.editOriginal("Ein Fehler ist aufgetreten.").queue();
                            DataApi.getDataInstance().logError(getClass(), "Failed to create whitelist entry.");
                            return;
                        }

                        Whitelist whitelistCreate = whitelistCreateOptional.get();

                        Guild guild = interaction.getGuild();
                        if (guild != null) {
                            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);
                            Role whitelistedRole = discordGuild.getWhitelistedRole();

                            if (whitelistedRole != null) {
                                guild.addRoleToMember(user, whitelistedRole).queue();
                            }
                        }

                        Whitelist.getWhitelistQueryEmbed(whitelistCreate).whenComplete(embed -> {
                            hook.deleteOriginal().queue();
                            channel.sendMessage(user.getAsMention() + " befindet sich nun auf der Whitelist.")
                                    .setEmbeds(embed).queue();
                        }, throwable -> {
                            hook.editOriginal("Ein Fehler ist aufgetreten.").queue();
                            throwable.printStackTrace();
                        });
                    }, throwable -> {
                        hook.editOriginal("Ein Fehler ist aufgetreten.").queue();
                        throwable.printStackTrace();
                    });
                }, throwable -> {
                    hook.editOriginal("Ein Fehler ist aufgetreten.").queue();
                    throwable.printStackTrace();
                });
            }, throwable -> {
                hook.editOriginal("Ein Fehler ist aufgetreten.").queue();
                throwable.printStackTrace();
            });
        });
    }

}
