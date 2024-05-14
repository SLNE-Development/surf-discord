package dev.slne.discord.whitelist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The type Whitelist.
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Whitelist {

	@JsonProperty("id")
	private long id;

	@JsonProperty("uuid")
	private UUID uuid;

	@JsonProperty("minecraft_name")
	private String minecraftName;

	@JsonProperty("twitch_link")
	private String twitchLink;

	@JsonProperty("discord_id")
	private String discordId;

	@JsonProperty("added_by_id")
	private String addedById;

	@JsonProperty("added_by_name")
	private String addedByName;

	@JsonProperty("added_by_avatar_url")
	private String addedByAvatarUrl;

	@JsonProperty("blocked")
	@Setter
	private boolean blocked;

	@JsonProperty("created_at")
	private ZonedDateTime createdAt;

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
	 * Instantiates a new Whitelist.
	 *
	 * @param uuid       the uuid
	 * @param twitchLink the twitch link
	 * @param discordId  the discord id
	 * @param addedById  the added by id
	 */
	public Whitelist(UUID uuid, String twitchLink, String discordId, String addedById) {
		this.uuid = uuid;
		this.twitchLink = twitchLink;
		this.discordId = discordId;
		this.addedById = addedById;
	}

	/**
	 * Returns if a {@link User} is whitelisted.
	 *
	 * @param user The {@link User}.
	 *
	 * @return The {@link CompletableFuture}.
	 */
	public static CompletableFuture<Boolean> isWhitelisted(User user) {
		return WhitelistService.INSTANCE.getWhitelistByDiscordId(user.getId()).thenApply(Objects::nonNull)
										.exceptionally(e -> false);
	}

	/**
	 * Returns a {@link MessageEmbed} for a {@link Whitelist}.
	 *
	 * @param whitelist The {@link Whitelist}.
	 *
	 * @return The {@link MessageEmbed}.
	 */
	public static @Nonnull CompletableFuture<MessageEmbed> getWhitelistQueryEmbed(Whitelist whitelist) {
		CompletableFuture<MessageEmbed> future = new CompletableFuture<>();

		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("Whitelist Query");
		builder.setFooter("Whitelist Query", DiscordBot.getInstance().getJda().getSelfUser().getAvatarUrl());
		builder.setDescription("Whitelist Informationen");
		builder.setColor(0x000000);
		builder.setTimestamp(Instant.now());

		DataApi.getNameByPlayerUuid(whitelist.getUuid()).thenAcceptAsync(name -> {
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

			CompletableFuture.allOf(finaldiscordUserFuture, finalAddedByFuture).thenAccept(v -> {
				User discordUser = finaldiscordUserFuture.join();
				User addedBy = finalAddedByFuture.join();

				if (name != null) {
					builder.addField("Minecraft Name", name, true);
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
	 * Creates a new {@link Whitelist}.
	 *
	 * @return The {@link CompletableFuture}.
	 */
	public CompletableFuture<Whitelist> create() {
		return WhitelistService.INSTANCE.addWhitelist(this);
	}

	/**
	 * Updates the {@link Whitelist}.
	 *
	 * @return The {@link CompletableFuture}.
	 */
	public CompletableFuture<Whitelist> update() {
		return WhitelistService.INSTANCE.updateWhitelist(this);
	}

	/**
	 * Gets added by.
	 *
	 * @return the addedBy
	 */
	@JsonIgnore
	public RestAction<User> getAddedBy() {
		if (addedById == null) {
			return null;
		}

		return DiscordBot.getInstance().getJda().retrieveUserById(addedById);
	}

	/**
	 * Gets discord user.
	 *
	 * @return the discordUser
	 */
	@JsonIgnore
	public RestAction<User> getDiscordUser() {
		if (discordId == null) {
			return null;
		}

		return DiscordBot.getInstance().getJda().retrieveUserById(discordId);
	}
}
