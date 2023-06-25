package dev.slne.discord.discord.interaction.button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public abstract class DiscordButton {

    private @Nonnull String id;
    private @Nonnull String label;
    private @Nullable Emoji emoji;
    private @Nonnull ButtonStyle style;

    /**
     * The DiscordButton
     *
     * @param id    the id of the button
     * @param label the label of the button
     * @param emoji the icon of the button
     */
    protected DiscordButton(@Nonnull String id, @Nonnull String label, @Nullable Emoji emoji,
            @Nonnull ButtonStyle style) {
        this.id = id;
        this.label = label;
        this.emoji = emoji;
        this.style = style;
    }

    /**
     * Forms the button
     *
     * @return the button
     */
    public Button formDiscordButton() {
        return Button.of(style, id, label, emoji);
    }

    /**
     * The action of the button
     *
     * @param interaction the interaction
     */
    public abstract void onClick(ButtonInteraction interaction);

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the emoji
     */
    public Emoji getEmoji() {
        return emoji;
    }

    /**
     * @return the style
     */
    public ButtonStyle getStyle() {
        return style;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

}
