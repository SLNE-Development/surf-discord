package dev.slne.discord.discord.interaction.button;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DiscordButton {

    private final @Nonnull String id;
    private final @Nonnull String label;
    private final @Nullable Emoji emoji;
    private final @Nonnull ButtonStyle style;

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
    @SuppressWarnings("unused")
    public @NotNull String getId() {
        return id;
    }

    /**
     * @return the emoji
     */
    @SuppressWarnings("unused")
    public @Nullable Emoji getEmoji() {
        return emoji;
    }

    /**
     * @return the style
     */
    @SuppressWarnings("unused")
    public @NotNull ButtonStyle getStyle() {
        return style;
    }

    /**
     * @return the label
     */
    @SuppressWarnings("unused")
    public @NotNull String getLabel() {
        return label;
    }

}
