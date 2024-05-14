package dev.slne.discord.datasource;

import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.CompletableFuture;

/**
 * The type Webhook helper.
 */
public class WebhookHelper {

	/**
	 * Private constructor to prevent instantiation
	 */
	private WebhookHelper() {

	}

	/**
	 * Gets a webhook from a channel
	 *
	 * @param channel   The channel to get the webhook from
	 * @param webhookId The webhook id to get
	 *
	 * @return The webhook
	 */
	public static CompletableFuture<Webhook> getWebhook(TextChannel channel, String webhookId) {
		CompletableFuture<Webhook> future = new CompletableFuture<>();

		channel.retrieveWebhooks().queue(
				webhooks ->
						future.complete(
								webhooks.stream().filter(hook -> hook.getId().equals(webhookId)).findFirst()
										.orElse(null)),
				future::completeExceptionally
		);

		return future;
	}

}
