package dev.slne.discord.discord.interaction.button.buttons;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.annotation.DiscordButton;
import dev.slne.discord.annotation.DiscordEmoji;
import dev.slne.discord.discord.interaction.button.DiscordButtonHandler;
import dev.slne.discord.discord.interaction.select.DiscordSelectMenu;
import dev.slne.discord.discord.interaction.select.menus.TicketsMenu;
import dev.slne.discord.message.EmbedColors;
import dev.slne.discord.message.RawMessages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

/**
 * The type Open ticket button.
 */
@DiscordButton(
    id = OpenTicketButton.ID,
    label = "Ticket öffnen",
    style = ButtonStyle.SUCCESS,
    emoji = @DiscordEmoji(unicode = "🎫")
)
public class OpenTicketButton implements DiscordButtonHandler {

  public static final String ID = "open-ticket";

  @Override
  public void onClick(ButtonInteraction interaction) {
    DiscordSelectMenu selectMenu = new TicketsMenu(interaction.getId());
    DiscordBot.getInstance().getSelectMenuManager().addMenu(selectMenu);

    sendEmbed(selectMenu.build(), interaction);
  }

  /**
   * Send the embeds
   *
   * @param menu        the menu
   * @param interaction the interaction
   */
  private void sendEmbed(SelectMenu menu, ButtonInteraction interaction) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.setTitle(RawMessages.get("interaction.button.open-ticket.title"));
    builder.setDescription(RawMessages.get("interaction.button.open-ticket.description"));
    builder.setColor(EmbedColors.SELECT_TICKET_TYPE);

    interaction.deferReply(true)
        .queue(hook -> {
          hook.sendMessageEmbeds(builder.build())
              .setActionRow(menu)
              .setEphemeral(true)
              .queue();
        });
  }
}
