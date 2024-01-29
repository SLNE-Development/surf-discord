package dev.slne.discord.config.ticket;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

/**
 * The interface Ticket config.
 */
@ConfHeader("Ticket Config")
public interface TicketConfig {

	/**
	 * Whitelist message string.
	 *
	 * @return the string
	 */
	@AnnotationBasedSorter.Order(0)
	@ConfDefault.DefaultString(
			"Vielen Dank für deine Whitelist Anfrage. Du wirst nun auf die Whitelist hinzugefügt, sobald jemand aus dem Team Zeit findet. Wir bitten um etwas Geduld.")
	String whitelistMessage();

	/**
	 * Bugreport message string.
	 *
	 * @return the string
	 */
	@AnnotationBasedSorter.Order(1)
	@ConfDefault.DefaultString(
			"Wir freuen uns, dass du einen Fehler melden möchtest. **Bitte beschreibe das Problem so genau wie möglich**. Wann? Wie? Wo? Screenshots und Videos des Fehlers sind gerne gesehen.")
	String bugreportMessage();

	/**
	 * Server support message string.
	 *
	 * @return the string
	 */
	@AnnotationBasedSorter.Order(2)
	@ConfDefault.DefaultString("Willkommen beim Minecraft Server-Support!")
	String serverSupportMessage();

	/**
	 * Discord support message string.
	 *
	 * @return the string
	 */
	@AnnotationBasedSorter.Order(3)
	@ConfDefault.DefaultString("Willkommen beim Discord Server-Support!")
	String discordSupportMessage();

}
