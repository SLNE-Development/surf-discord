package dev.slne.discord.whitelist;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.gson.GsonConverter;
import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.web.WebRequest;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.API;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

public class Whitelist {

    private Optional<Long> id;
    private Optional<UUID> uuid;
    private Optional<String> minecraftName;
    private Optional<String> twitchLink;

    private Optional<RestAction<User>> discordUser;
    private Optional<String> discordId;

    private Optional<RestAction<User>> addedBy;
    private Optional<String> addedById;
    private Optional<String> addedByName;
    private Optional<String> addedByAvatarUrl;

    private boolean blocked;

    /**
     * Creates a new {@link Whitelist}.
     *
     * @param uuid          The uuid.
     * @param minecraftName The minecraft name.
     * @param twitchLink    The twitch link.
     * @param discordUser   The discord user.
     * @param addedBy       The user who added the whitelist.
     */
    public Whitelist(UUID uuid, String minecraftName, String twitchLink, User discordUser, User addedBy) {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        this.id = Optional.empty();
        this.uuid = Optional.of(uuid);
        this.minecraftName = Optional.of(minecraftName);
        this.twitchLink = Optional.of(twitchLink);

        this.discordUser = Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(discordUser.getId()));
        this.discordId = Optional.of(discordUser.getId());

        this.addedBy = Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(addedBy.getId()));
        this.addedById = Optional.of(addedBy.getId());
        this.addedByName = Optional.of(addedBy.getName());
        this.addedByAvatarUrl = Optional.of(addedBy.getAvatarUrl());

        this.blocked = false;
    }

    /**
     * Creates a new {@link Whitelist}.
     *
     * @param copy The {@link Whitelist} to copy.
     */
    public Whitelist(Whitelist copy) {
        this.id = copy.id;
        this.uuid = copy.uuid;
        this.minecraftName = copy.minecraftName;
        this.twitchLink = copy.twitchLink;

        this.discordUser = copy.discordUser;
        this.discordId = copy.discordId;

        this.addedBy = copy.addedBy;
        this.addedById = copy.addedById;
        this.addedByName = copy.addedByName;
        this.addedByAvatarUrl = copy.addedByAvatarUrl;

        this.blocked = copy.blocked;
    }

    /**
     * Creates a new {@link Whitelist}.
     *
     * @param id               The id.
     * @param uuid             The uuid.
     * @param minecraftName    The minecraft name.
     * @param twitchLink       The twitch link.
     * @param discordId        The discord id.
     * @param addedBy          The user who added the whitelist.
     * @param addedById        the id of the user who added the whitelist
     * @param addedByName      the name of the user who added the whitelist
     * @param addedByAvatarUrl the avatar url of the user who added the whitelist
     * @param blocked          Whether the whitelist is blocked.
     */
    @SuppressWarnings("java:S107")
    private Whitelist(Optional<Long> id, Optional<UUID> uuid, Optional<String> minecraftName,
            Optional<String> twitchLink, Optional<RestAction<User>> discordUser, Optional<String> discordId,
            Optional<RestAction<User>> addedBy, Optional<String> addedById, Optional<String> addedByName,
            Optional<String> addedByAvatarUrl, boolean blocked) {
        this.id = id;
        this.uuid = uuid;
        this.minecraftName = minecraftName;
        this.twitchLink = twitchLink;
        this.discordUser = discordUser;
        this.discordId = discordId;
        this.addedBy = addedBy;
        this.addedById = addedById;
        this.addedByName = addedByName;
        this.addedByAvatarUrl = addedByAvatarUrl;
        this.blocked = blocked;
    }

    /**
     * Returns if a {@link User} is whitelisted.
     *
     * @param user The {@link User}.
     * @return The {@link SurfFutureResult}.
     */
    public static SurfFutureResult<Boolean> isWhitelisted(User user) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        DiscordFutureResult<Boolean> futureResult = new DiscordFutureResult<>(future);

        getWhitelists(null, user.getId(), null).whenComplete(whitelistsOptional -> {
            if (whitelistsOptional.isEmpty()) {
                future.complete(false);
                return;
            }

            List<Whitelist> whitelists = whitelistsOptional.get();

            future.complete(!whitelists.isEmpty());
        }, future::completeExceptionally);

        return futureResult;
    }

    /**
     * Returns a {@link MessageEmbed} for a {@link Whitelist}.
     *
     * @param whitelist The {@link Whitelist}.
     * @return The {@link MessageEmbed}.
     */
    @SuppressWarnings({ "java:S3776", "java:S1192" })
    public static @Nonnull SurfFutureResult<MessageEmbed> getWhitelistQueryEmbed(Whitelist whitelist) {
        CompletableFuture<MessageEmbed> future = new CompletableFuture<>();
        DiscordFutureResult<MessageEmbed> futureResult = new DiscordFutureResult<>(future);

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Whitelist Query");
        builder.setFooter("Whitelist Query", DiscordBot.getInstance().getJda().getSelfUser().getAvatarUrl());
        builder.setDescription("Whitelist Informationen");
        builder.setColor(0x000000);
        builder.setTimestamp(Instant.now());

        Optional<UUID> uuid = whitelist.getUuid();
        Optional<String> minecraftName = whitelist.getMinecraftName();
        Optional<String> twitchLink = whitelist.getTwitchLink();
        Optional<RestAction<User>> discordUserRest = whitelist.getDiscordUser();
        Optional<RestAction<User>> addedByRest = whitelist.getAddedBy();

        CompletableFuture<User> discordUserFuture = new CompletableFuture<>();
        CompletableFuture<User> addedByFuture = new CompletableFuture<>();

        if (discordUserRest.isPresent()) {
            discordUserFuture = discordUserRest.get().submit();
        } else {
            discordUserFuture.complete(null);
        }

        if (addedByRest.isPresent()) {
            addedByFuture = addedByRest.get().submit();
        } else {
            addedByFuture.complete(null);
        }

        final CompletableFuture<User> finaldiscordUserFuture = discordUserFuture;
        final CompletableFuture<User> finalAddedByFuture = addedByFuture;

        CompletableFuture.allOf(finaldiscordUserFuture, finalAddedByFuture).thenAccept(v -> {
            User discordUser = finaldiscordUserFuture.join();
            User addedBy = finalAddedByFuture.join();

            String minecraftNameString = minecraftName.orElse(null);
            String twitchLinkString = twitchLink.orElse(null);
            UUID uuidObject = uuid.orElse(null);
            String uuidString = null;
            if (uuidObject != null) {
                uuidString = uuidObject.toString();
            }

            if (minecraftName.isPresent() && minecraftNameString != null) {
                builder.addField("Minecraft Name", minecraftNameString, true);
            }

            if (twitchLink.isPresent() && twitchLinkString != null) {
                builder.addField("Twitch Link", twitchLinkString, true);
            }

            if (discordUser != null) {
                builder.addField("Discord User", discordUser.getAsMention(), true);
            }

            if (addedBy != null) {
                builder.addField("Added By", addedBy.getAsMention(), true);
            }

            if (uuid.isPresent() && uuidString != null) {
                builder.addField("UUID", uuidString, false);
            }

            future.complete(builder.build());
        }).exceptionally(exception -> {
            future.completeExceptionally(exception);
            return null;
        });

        return futureResult;
    }

    /**
     * Finds any {@link Whitelist}s by a uuid, discord id or twitch link.
     *
     * @param uuid       The uuid.
     * @param discordId  The discord id.
     * @param twitchLink The twitch link.
     * @return The {@link Whitelist}.
     */
    @SuppressWarnings({ "java:S3776", "java:S1192" })
    public static SurfFutureResult<Optional<List<Whitelist>>> getWhitelists(UUID uuid, String discordId,
            String twitchLink) {
        CompletableFuture<Optional<List<Whitelist>>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<List<Whitelist>>> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            List<Whitelist> whitelists = new ArrayList<>();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("uuid", uuid != null ? uuid.toString() : "");
            parameters.put("discord_id", discordId != null ? discordId : "");
            parameters.put("twitch_link", twitchLink != null ? twitchLink : "");

            WebRequest request = WebRequest.builder().json(true).url(API.WHITELIST_CHECK).parameters(parameters)
                    .build();
            request.executePost().thenAccept(response -> {
                if (response.getStatusCode() != 200) {
                    future.complete(Optional.empty());
                    return;
                }

                Object body = response.getBody();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonArray()) {
                    future.complete(Optional.empty());
                    return;
                }

                JsonArray jsonArray = (JsonArray) bodyElement.get("data");

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                    whitelists.add(fromJsonObject(jsonObject));
                }

                future.complete(Optional.of(whitelists));
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return futureResult;
    }

    /**
     * Finds a {@link Whitelist} by a minecraft name.
     *
     * @param minecraftName The minecraft name.
     * @return The {@link Whitelist}.
     */
    public static SurfFutureResult<Optional<Whitelist>> getWhitelistByMinecraftName(String minecraftName) {
        CompletableFuture<Optional<Whitelist>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<Whitelist>> futureResult = new DiscordFutureResult<>(future);

        UUIDResolver.resolve(minecraftName).whenComplete(uuidMinecraftNameOptional -> {
            if (!uuidMinecraftNameOptional.isPresent()) {
                future.complete(Optional.empty());
                return;
            }

            UUID uuid = uuidMinecraftNameOptional.get().uuid();

            getWhitelistByUUID(uuid).whenComplete(whitelistOptional -> {
                if (!whitelistOptional.isPresent()) {
                    future.complete(Optional.empty());
                    return;
                }

                future.complete(whitelistOptional);
            });
        }, future::completeExceptionally);

        return futureResult;
    }

    /**
     * Finds a {@link Whitelist} by a uuid.
     *
     * @param uuid The uuid.
     * @return The {@link Whitelist}.
     */
    public static SurfFutureResult<Optional<Whitelist>> getWhitelistByUUID(UUID uuid) {
        CompletableFuture<Optional<Whitelist>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<Whitelist>> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            String url = String.format(API.WHITELIST, uuid.toString());
            WebRequest request = WebRequest.builder().json(true).url(url).build();

            request.executeGet().thenAccept(response -> {
                if (response.getStatusCode() != 200) {
                    future.complete(Optional.empty());
                    return;
                }

                Object body = response.getBody();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                    future.complete(Optional.empty());
                    return;
                }

                JsonObject jsonObject = (JsonObject) bodyElement.get("data");

                future.complete(Optional.of(fromJsonObject(jsonObject)));
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return futureResult;
    }

    /**
     * Finds a {@link Whitelist} by a discordId.
     *
     * @param discordId The discordId.
     * @return The {@link Whitelist}.
     */
    public static SurfFutureResult<Optional<Whitelist>> getWhitelistByDiscordId(String discordId) {
        CompletableFuture<Optional<Whitelist>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<Whitelist>> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            String url = String.format(API.WHITELIST_BY_DISCORD_ID, discordId);
            WebRequest request = WebRequest.builder().json(true).url(url).build();

            request.executeGet().thenAccept(response -> {
                if (response.getStatusCode() != 200) {
                    future.complete(Optional.empty());
                    return;
                }

                Object body = response.getBody();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                    future.complete(Optional.empty());
                    return;
                }

                JsonObject jsonObject = (JsonObject) bodyElement.get("data");

                future.complete(Optional.of(fromJsonObject(jsonObject)));
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return futureResult;
    }

    /**
     * Returns all whitelists
     *
     * @return The List of {@link Whitelist}.
     */
    public static SurfFutureResult<Optional<List<Whitelist>>> getAllWhitelists() {
        CompletableFuture<Optional<List<Whitelist>>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<List<Whitelist>>> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            WebRequest request = WebRequest.builder().json(true).url(API.WHITELISTS).build();
            request.executeGet().thenAccept(response -> {
                List<Whitelist> whitelists = new ArrayList<>();

                if (response.getStatusCode() != 200) {
                    future.complete(Optional.of(whitelists));
                    return;
                }

                Object body = response.getBody();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonArray()) {
                    future.complete(Optional.of(whitelists));
                    return;
                }

                JsonArray jsonArray = (JsonArray) bodyElement.get("data");

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                    whitelists.add(fromJsonObject(jsonObject));
                }

                future.complete(Optional.of(whitelists));
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return futureResult;
    }

    /**
     * Creates a new {@link Whitelist} from a {@link JsonObject}.
     *
     * @param jsonObject The {@link JsonObject}.
     * @return The {@link Whitelist}.
     */
    @SuppressWarnings("java:S3776")
    private static Whitelist fromJsonObject(JsonObject jsonObject) {
        Optional<Long> id = Optional.empty();
        Optional<UUID> uuid = Optional.empty();
        Optional<String> minecraftName = Optional.empty();
        Optional<String> twitchLink = Optional.empty();
        Optional<RestAction<User>> discordUser = Optional.empty();
        Optional<String> discordId = Optional.empty();
        Optional<RestAction<User>> addedBy = Optional.empty();
        Optional<String> addedById = Optional.empty();
        Optional<String> addedByName = Optional.empty();
        Optional<String> addedByAvatarUrl = Optional.empty();
        boolean blocked = false;

        if (jsonObject.has("id") && jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()) {
            id = Optional.of(jsonObject.get("id").getAsLong());
        }

        if (jsonObject.has("uuid") && jsonObject.get("uuid") != null && !jsonObject.get("uuid").isJsonNull()) {
            uuid = Optional.of(UUID.fromString(jsonObject.get("uuid").getAsString()));
        }

        if (jsonObject.has("twitch_link") && jsonObject.get("twitch_link") != null
                && !jsonObject.get("twitch_link").isJsonNull()) {
            twitchLink = Optional.of(jsonObject.get("twitch_link").getAsString());
        }

        if (jsonObject.has("discord_id") && jsonObject.get("discord_id") != null
                && !jsonObject.get("discord_id").isJsonNull()) {
            discordId = Optional.of(jsonObject.get("discord_id").getAsString());

            String discordIdString = discordId.orElse(null);
            if (discordId.isPresent() && discordIdString != null) {
                discordUser = Optional.ofNullable(
                        DiscordBot.getInstance().getJda().retrieveUserById(discordIdString));
            }
        }

        if (jsonObject.has("added_by_id") && jsonObject.get("added_by_id") != null
                && !jsonObject.get("added_by_id").isJsonNull()) {
            addedById = Optional.of(jsonObject.get("added_by_id").getAsString());

            String addedByIdString = addedById.orElse(null);
            if (addedById.isPresent() && addedByIdString != null) {
                addedBy = Optional.ofNullable(
                        DiscordBot.getInstance().getJda().retrieveUserById(addedByIdString));
            }
        }

        if (jsonObject.has("added_by_name") && jsonObject.get("added_by_name") != null
                && !jsonObject.get("added_by_name").isJsonNull()) {
            addedByName = Optional.of(jsonObject.get("added_by_name").getAsString());
        }

        if (jsonObject.has("added_by_avatar_url") && jsonObject.get("added_by_avatar_url") != null
                && !jsonObject.get("added_by_avatar_url").isJsonNull()) {
            addedByAvatarUrl = Optional.of(jsonObject.get("added_by_avatar_url").getAsString());
        }

        if (jsonObject.has("blocked") && jsonObject.get("blocked") != null
                && !jsonObject.get("blocked").isJsonNull()) {
            blocked = jsonObject.get("blocked").getAsBoolean();
        }

        Optional<UuidMinecraftName> uuidMinecraftName = UUIDResolver.resolve(uuid.get()).join();
        if (uuidMinecraftName.isPresent()) {
            minecraftName = Optional.ofNullable(uuidMinecraftName.get().minecraftName());
        }

        return new Whitelist(id, uuid, minecraftName, twitchLink, discordUser, discordId, addedBy, addedById,
                addedByName, addedByAvatarUrl, blocked);
    }

    /**
     * Creates a new {@link Whitelist}.
     *
     * @return The {@link SurfFutureResult}.
     */
    public SurfFutureResult<Optional<Whitelist>> create() {
        CompletableFuture<Optional<Whitelist>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<Whitelist>> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            WebRequest request = WebRequest.builder().json(true).parameters(toParameters()).url(API.WHITELISTS).build();
            request.executePost().thenAccept(response -> {
                if (!(response.getStatusCode() == 200 || response.getStatusCode() == 201)) {
                    future.complete(Optional.empty());
                    return;
                }

                Object body = response.getBody();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                    future.complete(Optional.empty());
                    return;
                }

                JsonObject jsonObject = (JsonObject) bodyElement.get("data");

                Whitelist tempWhitelist = fromJsonObject(jsonObject);
                id = tempWhitelist.id;

                future.complete(Optional.of(this));
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return futureResult;
    }

    /**
     * Updates the {@link Whitelist}.
     *
     * @return The {@link SurfFutureResult}.
     */
    public SurfFutureResult<Optional<Whitelist>> update() {
        CompletableFuture<Optional<Whitelist>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<Whitelist>> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            String url = String.format(API.WHITELIST, uuid.get().toString());
            WebRequest request = WebRequest.builder().json(true).parameters(toParameters()).url(url).build();
            request.executePost().thenAccept(response -> {
                if (!(response.getStatusCode() == 200 || response.getStatusCode() == 201)) {
                    future.complete(Optional.empty());
                    return;
                }

                Object body = response.getBody();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                    future.complete(Optional.empty());
                    return;
                }

                future.complete(Optional.of(this));
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return futureResult;
    }

    /**
     * Converts the {@link Whitelist} to a {@link Map} of parameters.
     *
     * @return The {@link Map} of parameters.
     */
    public Map<String, String> toParameters() {
        Map<String, String> parameters = new HashMap<>();

        if (uuid.isPresent()) {
            parameters.put("uuid", uuid.get().toString());
        }

        if (twitchLink.isPresent()) {
            parameters.put("twitch_link", twitchLink.get());
        }

        if (discordId.isPresent()) {
            parameters.put("discord_id", discordId.get());
        }

        if (addedById.isPresent()) {
            parameters.put("added_by_id", addedById.get());
        }

        if (addedByName.isPresent()) {
            parameters.put("added_by_name", addedByName.get());
        }

        if (addedByAvatarUrl.isPresent()) {
            parameters.put("added_by_avatar_url", addedByAvatarUrl.get());
        }

        parameters.put("blocked", blocked ? "1" : "0");

        return parameters;
    }

    /**
     * @return the addedBy
     */
    public Optional<RestAction<User>> getAddedBy() {
        return addedBy;
    }

    /**
     * @return the addedByAvatarUrl
     */
    public Optional<String> getAddedByAvatarUrl() {
        return addedByAvatarUrl;
    }

    /**
     * @return the addedById
     */
    public Optional<String> getAddedById() {
        return addedById;
    }

    /**
     * @return the addedByName
     */
    public Optional<String> getAddedByName() {
        return addedByName;
    }

    /**
     * @return the discordId
     */
    public Optional<String> getDiscordId() {
        return discordId;
    }

    /**
     * @return the id
     */
    public Optional<Long> getId() {
        return id;
    }

    /**
     * @return the twitchLink
     */
    public Optional<String> getTwitchLink() {
        return twitchLink;
    }

    /**
     * @return the uuid
     */
    public Optional<UUID> getUuid() {
        return uuid;
    }

    /**
     * @return the minecraftName
     */
    public Optional<String> getMinecraftName() {
        return minecraftName;
    }

    /**
     * @return the blocked
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * @return the discordUser
     */
    public Optional<RestAction<User>> getDiscordUser() {
        return discordUser;
    }

    /**
     * @param blocked the blocked to set
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

}
