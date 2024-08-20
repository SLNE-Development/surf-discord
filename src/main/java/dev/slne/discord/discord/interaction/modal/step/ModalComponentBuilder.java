package dev.slne.discord.discord.interaction.modal.step;

import java.util.LinkedList;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import org.jetbrains.annotations.Contract;

/**
 * A builder class for constructing modal components.
 */
@Getter
public final class ModalComponentBuilder {

  private final LinkedList<ActionComponent> components = new LinkedList<>();

  /**
   * Adds a component to the builder.
   *
   * @param component The component to add.
   * @return The current ModalComponentBuilder instance.
   */
  @Contract("_ -> this")
  public ModalComponentBuilder addComponent(ActionComponent component) {
    components.add(component);

    return this;
  }

  /**
   * Adds a component to the beginning of the list.
   *
   * @param component The component to add.
   * @return The current ModalComponentBuilder instance.
   */
  @Contract("_ -> this")
  public ModalComponentBuilder addFirstComponent(ActionComponent component) {
    components.addFirst(component);

    return this;
  }

  /**
   * Adds a component to the end of the list.
   *
   * @param component The component to add.
   * @return The current ModalComponentBuilder instance.
   */
  @Contract("_ -> this")
  public ModalComponentBuilder addLastComponent(ActionComponent component) {
    components.addLast(component);

    return this;
  }

  /**
   * Adds a component at the specified index.
   *
   * @param index     The position to insert the component at.
   * @param component The component to add.
   * @return The current ModalComponentBuilder instance.
   */
  @Contract("_, _ -> this")
  public ModalComponentBuilder addComponent(int index, ActionComponent component) {
    components.add(index, component);

    return this;
  }
}
