package dev.slne.discord.discord.interaction.modal.step;

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

  public final ItemComponent createSelection() {
    return StringSelectMenu.create(id)
        .addOptions(options)
        .setMaxValues(1)
        .build();
  }

  private void setSelected(String selected, StringSelectInteractionEvent event) {
    this.selected = selected;
    selectionFuture.complete(event);
  }

  private static String generateId() {
    return "surf-selection-step-" + counter.getAndIncrement();
  }

  public static class ModalSelectionStepListener extends ListenerAdapter {

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
