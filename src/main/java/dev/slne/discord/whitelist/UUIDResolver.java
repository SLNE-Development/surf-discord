package dev.slne.discord.whitelist;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.slne.data.api.web.WebRequest;
import dev.slne.discord.DiscordBot;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDResolver {

    /**
     * Private constructor to hide the implicit public one.
     */
    private UUIDResolver() {
    }

    /**
     * Resolves a minecraft name to a UUID.
     *
     * @param uuidOrMinecraftName The uuid or minecraft name.
     *
     * @return The UUID.
     */
    @SuppressWarnings({ "java:S3358", "java:S3776", "java:S1192" })
    public static CompletableFuture<UuidMinecraftName> resolve(Object uuidOrMinecraftName) {
        CompletableFuture<UuidMinecraftName> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            UUIDCache cache = DiscordBot.getInstance().getUuidCache();
            UuidMinecraftName cachedUuidMinecraftName = cache.hitCache(uuidOrMinecraftName);

            if (cachedUuidMinecraftName != null) {
                future.complete(cachedUuidMinecraftName);
                return;
            }

            String requestString = uuidOrMinecraftName instanceof UUID uuid ? uuid.toString()
                    : uuidOrMinecraftName instanceof String minecraftName ? minecraftName : null;

            if (requestString == null) {
                future.complete(null);
                return;
            }

            WebRequest request = WebRequest.builder().json(true).url("https://api.minetools.eu/uuid/" + requestString)
                    .build();

            request.executeGet().thenAccept(response -> {
                int statusCode = response.statusCode();

                if (!(statusCode >= 200 && statusCode < 300)) {
                    future.complete(null);
                    return;
                }

                Object body = response.body();
                String bodyString = body instanceof String string ? string : null;

                JsonObject jsonObject =
                        DiscordBot.getInstance().getGsonConverter().fromJson(bodyString, JsonObject.class);

                JsonElement idElement = jsonObject.get("id");
                JsonElement nameElement = jsonObject.get("name");

                if (idElement == null || nameElement == null || idElement.isJsonNull() || nameElement.isJsonNull()) {
                    future.complete(null);
                    return;
                }

                String idString = idElement.getAsString();
                String nameString = nameElement.getAsString();

                if (idString == null || nameString == null) {
                    future.complete(null);
                    return;
                }

                String dashedIdString = toDashedUuid(idString);
                UUID uuid = UUID.fromString(dashedIdString);

                future.complete(cache.setCache(nameString, uuid));
            }).exceptionally(throwable -> {
                future.completeExceptionally(throwable);
                return null;
            });
        });

        return future;
    }

    /**
     * Converts a undashed UUID to a dashed UUID.
     *
     * @param undashedUuid The undashed UUID.
     *
     * @return The UUID.
     */
    public static String toDashedUuid(String undashedUuid) {
        return undashedUuid.substring(0, 8) + "-" + undashedUuid.substring(8, 12) + "-" + undashedUuid.substring(12, 16)
                + "-" + undashedUuid.substring(16, 20) + "-" + undashedUuid.substring(20, 32);
    }

}
