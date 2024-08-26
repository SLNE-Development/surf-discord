package dev.slne.discord.discord.interaction.modal.step;

import dev.slne.discord.annotation.DiscordListener;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a step in a modal process that involves a selection component.
 */
@Getter
@Accessors(makeFinal = true)
public abstract class ModalSelectionStep extends ModalStep {

  private static final AtomicInteger counter = new AtomicInteger(0);
  private static final Object2ObjectMap<String, ModalSelectionStep> steps = new Object2ObjectOpenHashMap<>();

  private final String id;
  private final String selectTitle;
  private final SelectOption[] options;

  private final CompletableFuture<StringSelectInteractionEvent> selectionFuture = new CompletableFuture<>();
  private String selected;

  public ModalSelectionStep(String selectTitle, SelectOption... options) {
    this.selectTitle = selectTitle;
    this.options = options;
    this.id = generateId();

    steps.put(id, this);
  }

  /**
   * Creates the selection component for this step.
   *
   * @return The created selection component.
   */
  public final @NotNull ItemComponent createSelection() {
    return StringSelectMenu.create(id)
        .addOptions(options)
        .setMaxValues(1)
        .build();
  }

  /**
   * Sets the selected option for this step and completes the selection future.
   *
   * @param selected The selected option.
   * @param event    The selection event that triggered this action.
   */
  private void setSelected(String selected, StringSelectInteractionEvent event) {
    this.selected = selected;
    selectionFuture.complete(event);
  }

  /**
   * Generates a unique ID for this selection step.
   *
   * @return A unique ID string.
   */
  private static @NotNull String generateId() {
    return "surf-selection-step-" + counter.getAndIncrement();
  }

  /**
   * Listener class for handling selection interactions.
   */
  @DiscordListener
  public static class ModalSelectionStepListener extends ListenerAdapter {

    /**
     * Handles the selection interaction event and triggers the appropriate step.
     *
     * @param event The selection interaction event.
     */
    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
      final ModalSelectionStep step = steps.get(event.getComponentId());

      if (step == null) {
        return;
      }

      final String selected = event.getValues().getFirst();
      step.setSelected(selected, event);
    }
  }
}
