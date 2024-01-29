package dev.slne.discord;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.spring.SurfSpringApplication;
import dev.slne.discord.datasource.DiscordDataInstance;
import dev.slne.discord.ticket.TicketClient;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * The type Discord spring application.
 */
@SurfSpringApplication(scanBasePackages = "dev.slne.discord", scanFeignBasePackages = "dev.slne.discord")
public class DiscordSpringApplication {

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		DiscordDataInstance dataInstance = new DiscordDataInstance();
		new DataApi(dataInstance);

		ConfigurableApplicationContext context = DataApi.run(
				DiscordSpringApplication.class,
				DiscordSpringApplication.class.getClassLoader()
		);

		TicketClient ticketMemberClient = context.getBean(TicketClient.class);
		List<TestTicket> activeTickets = ticketMemberClient.getActiveTickets();

		for (TestTicket activeTicket : activeTickets) {
			System.out.println(activeTicket);
			System.out.println("#######################");
		}
	}

}
