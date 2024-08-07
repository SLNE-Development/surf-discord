package dev.slne.discord.punishment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;


/**
 * The type PunishmentBan.
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PunishmentBan {

	@JsonProperty("id")
	private long id;

	@JsonProperty("punishment_id")
	private String punishmentId;

	@JsonProperty("punished_uuid")
	private String punishedUuid;

	@Nullable
	@JsonProperty("server")
	private String server;

	@JsonProperty("security_ban")
	private boolean securityBan;

	@JsonProperty("permanent")
	private boolean permanent;

	@JsonProperty("raw_ban")
	private boolean rawBan;

	@Nullable
	@JsonProperty("reason")
	private String reason;

	@Nullable
	@JsonProperty("expiration_date")
	private String expirationDate;

	@Nullable
	@JsonProperty("issuer_uuid")
	private String issuerUuid;

	@JsonProperty("punishment_date")
	private String punishmentDate;

	@JsonProperty("unpunished")
	private boolean unpunished;

	@Nullable
	@JsonProperty("unpunished_date")
	private String unpunishedDate;

	@Nullable
	@JsonProperty("unpunished_issuer_uuid")
	private String unpunishedIssuerUuid;

	@JsonProperty("created_at")
	private String createdAt;

	@JsonProperty("updated_at")
	private String updatedAt;

}
