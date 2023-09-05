package dev.slne.discord.discord.interaction.command.commands.reactionrole;

import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.guild.reactionrole.ReactionRoleConfig;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnnecessaryUnicodeEscape")
public class ReactionRoleTextCommand extends DiscordCommand {

    /**
     * Creates a new ReactionRoleTextCommand.
     */
    public ReactionRoleTextCommand() {
        super("reactionrole", "Posted den reaction role text.");
    }

    @Override
    public @Nonnull List<SubcommandData> getSubCommands() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull List<OptionData> getOptions() {
        return new ArrayList<>();
    }

    @Override
    public @Nonnull DiscordPermission getPermission() {
        return DiscordPermission.USE_COMMAND_REACTION_ROLE_TEXT;
    }

    @Override
    public void execute(SlashCommandInteractionEvent interaction) {
        interaction.deferReply(true).queue(hook -> {
            Guild guild = interaction.getGuild();

            if (guild == null) {
                hook.editOriginal("Du musst auf einem Server sein, um diesen Command auszuführen!").queue();
                return;
            }

            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

            if (discordGuild == null) {
                hook.editOriginal("Dieser Server ist nicht registriert!").queue();
                return;
            }

            Channel channel = interaction.getChannel();

            if (!(channel instanceof TextChannel textChannel)) {
                hook.editOriginal("Dieser Command kann nur in Textkanälen ausgeführt werden!").queue();
                return;
            }

            MessageEmbed embed = getEmbed();

            textChannel.sendMessageEmbeds(embed).queue(message -> {
                ReactionRoleConfig currentConfig = discordGuild.getReactionRoleConfig();
                String reaction;

                if (currentConfig != null) {
                    reaction = currentConfig.reaction();
                } else {
                    reaction = "\u1F514";
                }

                Emoji emoji = Emoji.fromFormatted(reaction);

                hook.deleteOriginal().queue();
                message.addReaction(emoji).queue();

                if (currentConfig != null) {
                    String roleId = currentConfig.roleId();

                    discordGuild
                            .setReactionRoleConfig(new ReactionRoleConfig(message.getId(), message.getChannel().getId(),
                                    reaction, roleId));
                }
            });
        });
    }

    /**
     * Returns the embed.
     *
     * @return The embed.
     */
    private @Nonnull MessageEmbed getEmbed() {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Server Benachrichtigungs-Rolle");
        builder.setDescription(
                "Du möchtest benachrichtigt werden, wenn es neue Updates gibt? Dann reagiere mit :bell: unter dieser Nachricht!");
        builder.setColor(Color.decode("#ffaa39"));

        return builder.build();
    }

}
