package dev.slne.discord.datasource;

import java.util.concurrent.CompletableFuture;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

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
     * @return The webhook
     */
    public static SurfFutureResult<Webhook> getWebhook(TextChannel channel, String webhookId) {
        CompletableFuture<Webhook> future = new CompletableFuture<>();
        DiscordFutureResult<Webhook> result = new DiscordFutureResult<>(future);

        channel.retrieveWebhooks().queue(webhooks ->
            future.complete(webhooks.stream().filter(hook -> hook.getId().equals(webhookId)).findFirst().orElse(null))
        , future::completeExceptionally);

        return result;
    }

}
