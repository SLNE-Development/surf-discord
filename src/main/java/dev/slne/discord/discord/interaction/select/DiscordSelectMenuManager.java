package dev.slne.discord.discord.interaction.select;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Discord select menu manager.
 */
public class DiscordSelectMenuManager {

	private final List<DiscordSelectMenu> staticMenus;
	private final Cache<String, DiscordSelectMenu> menus;

	/**
	 * The DiscordSelectMenuManager
	 */
	public DiscordSelectMenuManager() {
		this.staticMenus = new ArrayList<>();
		this.menus = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(5)).build();
	}

	/**
	 * Adds a menu
	 *
	 * @param menu the menu
	 */
	public void addMenu(DiscordSelectMenu menu) {
		menus.put(menu.getId(), menu);
	}

	/**
	 * Add static menu.
	 *
	 * @param menu the menu
	 */
	public void addStaticMenu(DiscordSelectMenu menu) {
		staticMenus.add(menu);
	}

	/**
	 * Gets a button by its id
	 *
	 * @param id the id of the button
	 *
	 * @return the button
	 */
	public DiscordSelectMenu getMenu(String id) {
		return staticMenus.stream().filter(menu -> menu.getId().equals(id)).findFirst().orElse(menus.getIfPresent(id));
	}
}
