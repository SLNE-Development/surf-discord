package dev.slne.discord.discord.interaction.command.commands.ticket;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.button.DiscordButton;
import dev.slne.discord.discord.interaction.button.DiscordButtonManager;
import dev.slne.discord.discord.interaction.button.buttons.ticket.TicketButton;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class TicketButtonCommand extends DiscordCommand {

    private static final String SPACER = "~~[-------------------------------------------------------]~~";

    /**
     * Creates a new TicketButtonCommand.
     */
    public TicketButtonCommand() {
        super("ticket-buttons", "Print the ticket buttons.");
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
    public void execute(SlashCommandInteractionEvent interaction) {
        if (!(interaction.getChannel() instanceof TextChannel)) {
            return;
        }

        InteractionHook hook = interaction.deferReply(true).complete();
        hook.deleteOriginal().complete();

        TextChannel channel = (TextChannel) interaction.getChannel();

        sendSpacer(channel);
        sendEmbed(channel, "**Discord Support Ticket:**", getDiscordSupportText(), Color.BLUE);
        sendSpacer(channel);

        sendEmbed(channel, "**Server Support Ticket:**", getServerSupportText(), Color.GREEN);
        sendSpacer(channel);

        sendEmbed(channel, "**Bugreport Ticket:**", getBugreportText(), Color.RED);
        sendSpacer(channel);

        sendEmbed(channel, "**Whitelist Ticket:**", getWhitelistText(), Color.WHITE);
        sendSpacer(channel);

        DiscordButtonManager manager = DiscordBot.getInstance().getButtonManager();
        DiscordButton whitelistButton = manager.getButton(TicketButton.WHITELIST_TICKET_ID);
        DiscordButton serverSupportButton = manager.getButton(TicketButton.SERVER_SUPPORT_TICKET_ID);
        DiscordButton discordSupportButton = manager.getButton(TicketButton.DISCORD_SUPPORT_TICKET_ID);
        DiscordButton bugreportButton = manager.getButton(TicketButton.BUGREPORT_TICKET_ID);

        ActionRow row = ActionRow.of(
                discordSupportButton.formDiscordButton(),
                serverSupportButton.formDiscordButton(),
                bugreportButton.formDiscordButton(),
                whitelistButton.formDiscordButton());

        channel.sendMessageComponents(row).complete();
    }

    /**
     * Send the embeds
     *
     * @param channel          the channel
     * @param title            the title
     * @param embedDescription the description
     * @param color            the color
     */
    private void sendEmbed(TextChannel channel, String title, String embedDescription, Color color) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription(embedDescription);
        builder.setColor(color);

        channel.sendMessageEmbeds(builder.build()).queue();
    }

    /**
     * Send the ticket buttons
     *
     * @param channel The channel to send the buttons to
     */
    public void sendSpacer(TextChannel channel) {
        channel.sendMessage(SPACER).queue();
    }

    /**
     * Get the whitelist text
     *
     * @return The whitelist text
     */
    public String getWhitelistText() {
        StringBuilder builder = new StringBuilder();

        builder.append(
                "Um eine Whitelist Anfrage zu stellen reicht es, euren Discord Account mit eurem Twitch Account zu verbinden und danach ein **Whitelist Ticket** zu eröffnen.");

        builder.append("\n\n");
        builder.append("**Voraussetzungen**:\r\n" + //
                "- Ihr müsst CastCrafter auf Twitch folgen\r\n" + //
                "- Ihr müsst euren Discord Account mit Twitch verbunden haben");
        builder.append("\n\n");
        builder.append("**Wie verbinde ich meinen Twitch Account?**\r\n" + //
                "1. Klickt auf Benutzereinstellungen.\r\n" + //
                "2. Klickt auf den Punkt \"Verknüpfungen\".\r\n" + //
                "3. Klickt auf das \"Twitchsymbol\".\r\n" + //
                "4. Loggt euch mit eurem Twitch ein und wartet ab bis die Meldung erscheint:\r\n" + //
                "\"Connected your Twitch to Discord\"");
        builder.append("\n\n");
        builder.append("Achtet bitte darauf, dass eure verknüpften Accounts auch **öffentlich einsehbar** sind!");
        builder.append("\n\n");
        builder.append("**Wie trete ich dem Server bei und was gibt es zu beachten?**\r\n" + //
                "Die IP Adresse des Servers, sowie eine Übersicht über dessen Funktionen und Regeln findet ihr hier:");
        builder.append("\n\n");
        builder.append("https://www.castcrafter.de/server");

        return builder.toString();
    }

    /**
     * Get the bugreport text
     *
     * @return The bugreport text
     */
    public String getBugreportText() {
        StringBuilder builder = new StringBuilder();

        builder.append("Ihr möchtet einen Fehler auf dem Server melden, so verwendet bitte das **Bugreport Ticket**.");
        builder.append("\n\n");
        builder.append(
                "Bitte stellt den Fehler so genau wie möglich dar, damit wir uns ein genaues Bild machen können. Ein einfaches \"X und Y funktioniert nicht\" hilft uns nicht weiter.");
        builder.append("\n\n");
        builder.append(
                "Bitte beschreibt so genau wie möglich, **was ihr gemacht habt** und **was hätte passieren sollen**. Screenshots und vor allem Videos sind gerne gesehen.");

        return builder.toString();
    }

    /**
     * Get the server support text
     *
     * @return The server support text
     */
    public String getServerSupportText() {
        StringBuilder builder = new StringBuilder();

        builder.append(
                "Ihr habt **Fragen bezüglich der Minecraft-Serverregeln, möchtet einen Spieler melden** oder einen** Bann anfechten**, so habt ihr die Möglichkeit ein **Server Support Ticket** zu erstellen.");
        builder.append("\n\n");
        builder.append(
                "Bitte beachtet, dass diese Tickets nur für Anliegen auf dem Community-Server gedacht sind. Bei anderen Anliegen können wir euch nicht helfen!");

        return builder.toString();
    }

    /**
     * Get the discord support text
     *
     * @return The discord support text
     */
    public String getDiscordSupportText() {
        StringBuilder builder = new StringBuilder();

        builder.append(
                "Ihr habt **Fragen bezüglich der Discord-Serverregeln** oder **möchtet einen Spieler melden**, so habt ihr die Möglichkeit ein **Discord Support Ticket** zu erstellen.");

        return builder.toString();
    }

}
