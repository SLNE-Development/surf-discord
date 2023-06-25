package dev.slne.discord.whitelist;

import java.util.Optional;
import java.util.UUID;

import com.google.gson.JsonObject;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.gson.GsonConverter;
import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.web.WebRequest;
import dev.slne.data.core.web.WebResponse;
import dev.slne.discord.DiscordBot;

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
    @SuppressWarnings("java:S3358")
    public static SurfFutureResult<Optional<UuidMinecraftName>> resolve(Object uuidOrMinecraftName) {
        return DataApi.getDataInstance().supplyAsync(() -> {
            UUIDCache cache = DiscordBot.getInstance().getUuidCache();
            Optional<UuidMinecraftName> cachedUuidMinecraftName = cache.hitCache(uuidOrMinecraftName);

            if (cachedUuidMinecraftName.isPresent()) {
                return cachedUuidMinecraftName;
            }

            String requestString = uuidOrMinecraftName instanceof UUID uuid ? uuid.toString()
                    : uuidOrMinecraftName instanceof String minecraftName ? minecraftName : null;

            if (requestString == null) {
                return Optional.empty();
            }

            WebRequest request = WebRequest.builder().json(true).url("https://api.minetools.eu/uuid/" + requestString)
                    .build();
            WebResponse response = request.executeGet().join();

            if (response.getStatusCode() != 200) {
                return Optional.empty();
            }

            Object body = response.getBody();
            String bodyString = body.toString();

            JsonObject jsonObject = new GsonConverter().fromJson(bodyString, JsonObject.class);

            String idString = jsonObject.get("id").getAsString();
            String nameString = jsonObject.get("name").getAsString();

            if (idString == null || nameString == null) {
                return Optional.empty();
            }

            String dashedIdString = toDashedUuid(idString);
            UUID uuid = UUID.fromString(dashedIdString);

            return cache.setCache(nameString, uuid);
        });
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
