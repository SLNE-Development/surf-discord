package dev.slne.discord.spring.feign.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * The type PunishmentBanDTO.
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class PunishmentBanDTO {
    @JsonProperty("id")
    private val id: Long = 0

    @JsonProperty("punishment_id")
    private val punishmentId: String? = null

    @JsonProperty("punished_uuid")
    private val punishedUuid: String? = null

    @Nullable
    @JsonProperty("server")
    private val server: String? = null

    @JsonProperty("security_ban")
    private val securityBan = false

    @JsonProperty("permanent")
    private val permanent = false

    @JsonProperty("raw_ban")
    private val rawBan = false

    @Nullable
    @JsonProperty("reason")
    private val reason: String? = null

    @Nullable
    @JsonProperty("expiration_date")
    private val expirationDate: String? = null

    @Nullable
    @JsonProperty("issuer_uuid")
    private val issuerUuid: String? = null

    @JsonProperty("punishment_date")
    private val punishmentDate: String? = null

    @JsonProperty("unpunished")
    private val unpunished = false

    @Nullable
    @JsonProperty("unpunished_date")
    private val unpunishedDate: String? = null

    @Nullable
    @JsonProperty("unpunished_issuer_uuid")
    private val unpunishedIssuerUuid: String? = null

    @JsonProperty("created_at")
    private val createdAt: String? = null

    @JsonProperty("updated_at")
    private val updatedAt: String? = null
}
