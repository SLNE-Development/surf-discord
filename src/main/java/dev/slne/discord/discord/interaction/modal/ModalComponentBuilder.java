package dev.slne.discord.discord.interaction.modal;

import java.util.LinkedList;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

@Getter
public class ModalComponentBuilder {
  private final LinkedList<ActionComponent> components = new LinkedList<>();

  public ModalComponentBuilder addComponent(ActionComponent component) {
    components.add(component);
    return this;
  }
}
