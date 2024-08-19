package dev.slne.discord.discord.interaction.modal.step;

import java.util.LinkedList;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

@Getter
public final class ModalComponentBuilder {
  private final LinkedList<ActionComponent> components = new LinkedList<>();

  public ModalComponentBuilder addComponent(ActionComponent component) {
    components.add(component);
    return this;
  }

  public ModalComponentBuilder addFirstComponent(ActionComponent component) {
    components.addFirst(component);
    return this;
  }

  public ModalComponentBuilder addLastComponent(ActionComponent component) {
    components.addLast(component);
    return this;
  }

  public ModalComponentBuilder addComponent(int index, ActionComponent component) {
    components.add(index, component);
    return this;
  }
}
