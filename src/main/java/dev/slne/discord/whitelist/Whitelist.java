package dev.slne.discord.whitelist;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.slne.data.api.gson.GsonConverter;
import dev.slne.data.api.web.WebRequest;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.API;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Whitelist {

    @SerializedName("id")
    private long id;

    @SerializedName("uuid")
    private UUID uuid;

    @SerializedName("twitch_link")
    private String twitchLink;

    @SerializedName("discord_id")
    private String discordId;

    @SerializedName("added_by_id")
    private String addedById;

    @SerializedName("added_by_name")
    private String addedByName;

    @SerializedName("added_by_avatar_url")
    private String addedByAvatarUrl;

    @SerializedName("blocked")
    private boolean blocked;
    private String minecraftName;

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

        this.uuid = uuid;
        this.twitchLink = twitchLink;
        this.minecraftName = minecraftName;

        if (discordUser != null) {
            this.discordId = discordUser.getId();
        }

        if (addedBy != null) {
            this.addedById = addedBy.getId();
            this.addedByName = addedBy.getName();
            this.addedByAvatarUrl = addedBy.getAvatarUrl();
        }

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
        this.twitchLink = copy.twitchLink;

        this.discordId = copy.discordId;

        this.addedById = copy.addedById;
        this.addedByName = copy.addedByName;
        this.addedByAvatarUrl = copy.addedByAvatarUrl;

        this.blocked = copy.blocked;
    }

    /**
     * Returns a {@link Whitelist} from a {@link JsonObject}.
     *
     * @param jsonObject The {@link JsonObject}.
     *
     * @return The {@link Whitelist}.
     */
    private static Whitelist fromJsonObject(JsonObject jsonObject) {
        GsonConverter gson = new GsonConverter();

        return gson.fromJson(jsonObject.toString(), Whitelist.class);
    }

    /**
     * Returns if a {@link User} is whitelisted.
     *
     * @param user The {@link User}.
     *
     * @return The {@link CompletableFuture}.
     */
    public static CompletableFuture<Boolean> isWhitelisted(User user) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        getWhitelists(null, user.getId(), null).thenAcceptAsync(
                whitelists -> future.complete(whitelists != null && !whitelists.isEmpty())).exceptionally(exception -> {
            future.completeExceptionally(exception);
            return null;
        });

        return future;
    }

    /**
     * Returns a {@link MessageEmbed} for a {@link Whitelist}.
     *
     * @param whitelist The {@link Whitelist}.
     *
     * @return The {@link MessageEmbed}.
     */
    @SuppressWarnings({ "java:S3776", "java:S1192" })
    public static @Nonnull CompletableFuture<MessageEmbed> getWhitelistQueryEmbed(Whitelist whitelist) {
        CompletableFuture<MessageEmbed> future = new CompletableFuture<>();

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Whitelist Query");
        builder.setFooter("Whitelist Query", DiscordBot.getInstance().getJda().getSelfUser().getAvatarUrl());
        builder.setDescription("Whitelist Informationen");
        builder.setColor(0x000000);
        builder.setTimestamp(Instant.now());

        UUIDResolver.resolve(whitelist.getUuid()).thenAcceptAsync(uuidMinecraftName -> {
            String minecraftName = null;

            if (uuidMinecraftName != null) {
                minecraftName = uuidMinecraftName.minecraftName();
            }

            UUID uuid = whitelist.getUuid();
            String twitchLink = whitelist.getTwitchLink();
            RestAction<User> discordUserRest = whitelist.getDiscordUser();
            RestAction<User> addedByRest = whitelist.getAddedBy();

            CompletableFuture<User> discordUserFuture = new CompletableFuture<>();
            CompletableFuture<User> addedByFuture = new CompletableFuture<>();

            if (discordUserRest != null) {
                discordUserFuture = discordUserRest.submit();
            } else {
                discordUserFuture.complete(null);
            }

            if (addedByRest != null) {
                addedByFuture = addedByRest.submit();
            } else {
                addedByFuture.complete(null);
            }

            final CompletableFuture<User> finaldiscordUserFuture = discordUserFuture;
            final CompletableFuture<User> finalAddedByFuture = addedByFuture;

            final String finalMinecraftName = minecraftName;
            CompletableFuture.allOf(finaldiscordUserFuture, finalAddedByFuture).thenAccept(v -> {
                User discordUser = finaldiscordUserFuture.join();
                User addedBy = finalAddedByFuture.join();

                if (finalMinecraftName != null) {
                    builder.addField("Minecraft Name", finalMinecraftName, true);
                }

                if (twitchLink != null) {
                    builder.addField("Twitch Link", twitchLink, true);
                }

                if (discordUser != null) {
                    builder.addField("Discord User", discordUser.getAsMention(), true);
                }

                if (addedBy != null) {
                    builder.addField("Added By", addedBy.getAsMention(), true);
                }

                if (uuid != null) {
                    builder.addField("UUID", uuid.toString(), false);
                }

                future.complete(builder.build());
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return future;
    }

    /**
     * Finds any {@link Whitelist}s by a uuid, discord id or twitch link.
     *
     * @param uuid       The uuid.
     * @param discordId  The discord id.
     * @param twitchLink The twitch link.
     *
     * @return The {@link Whitelist}.
     */
    @SuppressWarnings({ "java:S3776", "java:S1192" })
    public static CompletableFuture<List<Whitelist>> getWhitelists(UUID uuid, String discordId,
                                                                   String twitchLink) {
        CompletableFuture<List<Whitelist>> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            List<Whitelist> whitelists = new ArrayList<>();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("uuid", uuid != null ? uuid.toString() : "");
            parameters.put("discord_id", discordId != null ? discordId : "");
            parameters.put("twitch_link", twitchLink != null ? twitchLink : "");

            WebRequest request = WebRequest.builder().json(true).url(API.WHITELIST_CHECK).parameters(parameters)
                    .build();
            request.executePost().thenAccept(response -> {
                JsonArray jsonArray = response.bodyArray(new GsonConverter());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                    whitelists.add(fromJsonObject(jsonObject));
                }

                future.complete(whitelists);
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return future;
    }

    /**
     * Finds a {@link Whitelist} by a minecraft name.
     *
     * @param minecraftName The minecraft name.
     *
     * @return The {@link Whitelist}.
     */
    public static CompletableFuture<Whitelist> getWhitelistByMinecraftName(String minecraftName) {
        CompletableFuture<Whitelist> future = new CompletableFuture<>();

        UUIDResolver.resolve(minecraftName).thenAcceptAsync(uuidMinecraftName -> {
            if (uuidMinecraftName == null) {
                future.complete(null);
                return;
            }

            UUID uuid = uuidMinecraftName.uuid();

            getWhitelistByUUID(uuid).thenAcceptAsync(future::complete).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        }).exceptionally(exception -> {
            future.completeExceptionally(exception);
            return null;
        });

        return future;
    }

    /**
     * Finds a {@link Whitelist} by a uuid.
     *
     * @param uuid The uuid.
     *
     * @return The {@link Whitelist}.
     */
    public static CompletableFuture<Whitelist> getWhitelistByUUID(UUID uuid) {
        CompletableFuture<Whitelist> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            String url = String.format(API.WHITELIST, uuid.toString());
            WebRequest request = WebRequest.builder().json(true).url(url).build();

            request.executeGet().thenAccept(response -> {
                future.complete(fromJsonObject(response.bodyObject(new GsonConverter())));
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return future;
    }

    /**
     * Finds a {@link Whitelist} by a discordId.
     *
     * @param discordId The discordId.
     *
     * @return The {@link Whitelist}.
     */
    public static CompletableFuture<Whitelist> getWhitelistByDiscordId(String discordId) {
        CompletableFuture<Whitelist> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            String url = String.format(API.WHITELIST_BY_DISCORD_ID, discordId);
            WebRequest request = WebRequest.builder().json(true).url(url).build();

            request.executeGet().thenAccept(response -> {
                future.complete(fromJsonObject(response.bodyObject(new GsonConverter())));
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return future;
    }

    /**
     * Returns all whitelists
     *
     * @return The List of {@link Whitelist}.
     */
    public static CompletableFuture<List<Whitelist>> getAllWhitelists() {
        CompletableFuture<List<Whitelist>> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            WebRequest request = WebRequest.builder().json(true).url(API.WHITELISTS).build();
            request.executeGet().thenAccept(response -> {
                List<Whitelist> whitelists = new ArrayList<>();
                JsonArray jsonArray = response.bodyArray(new GsonConverter());

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                    whitelists.add(fromJsonObject(jsonObject));
                }

                future.complete(whitelists);
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return future;
    }

    /**
     * Creates a new {@link Whitelist}.
     *
     * @return The {@link CompletableFuture}.
     */
    public CompletableFuture<Whitelist> create() {
        CompletableFuture<Whitelist> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            WebRequest request = WebRequest.builder().json(true).parameters(toParameters()).url(API.WHITELISTS).build();
            request.executePost().thenAccept(response -> {
                Whitelist tempWhitelist = fromJsonObject(response.bodyObject(new GsonConverter()));
                id = tempWhitelist.id;

                future.complete(this);
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return future;
    }

    /**
     * Updates the {@link Whitelist}.
     *
     * @return The {@link CompletableFuture}.
     */
    public CompletableFuture<Whitelist> update() {
        CompletableFuture<Whitelist> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            String url = String.format(API.WHITELIST, uuid.toString());
            WebRequest request = WebRequest.builder().json(true).parameters(toParameters()).url(url).build();
            request.executePost().thenAccept(response -> {
                response.bodyObject(new GsonConverter());
                future.complete(this);
            }).exceptionally(exception -> {
                future.completeExceptionally(exception);
                return null;
            });
        });

        return future;
    }

    /**
     * Converts the {@link Whitelist} to a {@link Map} of parameters.
     *
     * @return The {@link Map} of parameters.
     */
    public Map<String, Object> toParameters() {
        Map<String, Object> parameters = new HashMap<>();

        if (uuid != null) {
            parameters.put("uuid", uuid.toString());
        }

        if (twitchLink != null) {
            parameters.put("twitch_link", twitchLink);
        }

        if (discordId != null) {
            parameters.put("discord_id", discordId);
        }

        if (addedById != null) {
            parameters.put("added_by_id", addedById);
        }

        if (addedByName != null) {
            parameters.put("added_by_name", addedByName);
        }

        if (addedByAvatarUrl != null) {
            parameters.put("added_by_avatar_url", addedByAvatarUrl);
        }

        if (minecraftName != null) {
            parameters.put("minecraft_name", minecraftName);
        }

        parameters.put("blocked", blocked ? "1" : "0");

        return parameters;
    }

    /**
     * @return the addedBy
     */
    public RestAction<User> getAddedBy() {
        if (addedById == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().retrieveUserById(addedById);
    }

    /**
     * @return the addedByAvatarUrl
     */
    public String getAddedByAvatarUrl() {
        return addedByAvatarUrl;
    }

    /**
     * @return the addedById
     */
    public String getAddedById() {
        return addedById;
    }

    /**
     * @return the addedByName
     */
    public String getAddedByName() {
        return addedByName;
    }

    /**
     * @return the discordId
     */
    public String getDiscordId() {
        return discordId;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the twitchLink
     */
    public String getTwitchLink() {
        return twitchLink;
    }

    /**
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return the minecraftName
     */
    public String getMinecraftName() {
        return minecraftName;
    }

    /**
     * @return the blocked
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * @param blocked the blocked to set
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * @return the discordUser
     */
    public RestAction<User> getDiscordUser() {
        if (discordId == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().retrieveUserById(discordId);
    }

}
