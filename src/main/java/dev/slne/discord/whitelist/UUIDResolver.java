package dev.slne.discord.whitelist;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.gson.GsonConverter;
import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.web.WebRequest;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;

public class UUIDResolver {

    /**
     * Private constructor to hide the implicit public one.
     */
    private UUIDResolver() {
    }

    /**
     * Resolves a minecraft name to a UUID.
     *
     * @param minecraftName The minecraft name.
     * @return The UUID.
     */
    @SuppressWarnings({ "java:S3358", "java:S3776", "java:S1192" })
    public static SurfFutureResult<Optional<UuidMinecraftName>> resolve(Object uuidOrMinecraftName) {
        CompletableFuture<Optional<UuidMinecraftName>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<UuidMinecraftName>> result = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            UUIDCache cache = DiscordBot.getInstance().getUuidCache();
            Optional<UuidMinecraftName> cachedUuidMinecraftName = cache.hitCache(uuidOrMinecraftName);

            if (cachedUuidMinecraftName.isPresent()) {
                future.complete(cachedUuidMinecraftName);
                return;
            }

            String requestString = uuidOrMinecraftName instanceof UUID uuid ? uuid.toString()
                    : uuidOrMinecraftName instanceof String minecraftName ? minecraftName : null;

            if (requestString == null) {
                future.complete(Optional.empty());
                return;
            }

            WebRequest request = WebRequest.builder().json(true).url("https://api.minetools.eu/uuid/" + requestString)
                    .build();

            request.executeGet().thenAccept(response -> {
                if (response.getStatusCode() != 200) {
                    future.complete(Optional.empty());
                    return;
                }

                Object body = response.getBody();
                String bodyString = body.toString();

                JsonElement jsonElement = new GsonConverter().fromJson(bodyString, JsonElement.class);

                if (jsonElement.isJsonNull()) {
                    future.complete(Optional.empty());
                    return;
                }

                JsonObject jsonObject = jsonElement.getAsJsonObject();

                JsonElement idElement = jsonObject.get("id");
                JsonElement nameElement = jsonObject.get("name");

                if (idElement == null || nameElement == null || idElement.isJsonNull() || nameElement.isJsonNull()) {
                    future.complete(Optional.empty());
                    return;
                }

                String idString = idElement.getAsString();
                String nameString = nameElement.getAsString();

                if (idString == null || nameString == null) {
                    future.complete(Optional.empty());
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

        return result;
    }

    /**
     * Converts a undashed UUID to a dashed UUID.
     *
     * @param undashedUuid The undashed UUID.
     * @return The UUID.
     */
    public static String toDashedUuid(String undashedUuid) {
        return undashedUuid.substring(0, 8) + "-" + undashedUuid.substring(8, 12) + "-" + undashedUuid.substring(12, 16)
                + "-" + undashedUuid.substring(16, 20) + "-" + undashedUuid.substring(20, 32);
    }

}
