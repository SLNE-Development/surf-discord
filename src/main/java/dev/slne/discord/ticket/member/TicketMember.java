package dev.slne.discord.ticket.member;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.gson.GsonConverter;
import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.web.WebRequest;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.datasource.API;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.ticket.Ticket;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

public class TicketMember {

    private Optional<Long> id;

    private Optional<RestAction<User>> member;
    private String memberId;
    private String memberName;
    private String memberAvatarUrl;

    private Optional<RestAction<User>> addedBy;
    private String addedById;
    private String addedByName;
    private String addedByAvatarUrl;

    private Optional<RestAction<User>> removedBy;
    private String removedById;
    private String removedByName;
    private String removedByAvatarUrl;

    private Ticket ticket;

    /**
     * Constructor for a ticket member
     *
     * @param member The member of the ticket
     */
    public TicketMember(Ticket ticket, User member, User addedBy) {
        this.id = Optional.empty();

        this.member = Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(member.getIdLong()));
        this.memberId = member.getId();
        this.memberName = member.getName();
        this.memberAvatarUrl = member.getAvatarUrl();

        this.addedBy = Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(addedBy.getIdLong()));
        this.addedById = addedBy.getId();
        this.addedByName = addedBy.getName();
        this.addedByAvatarUrl = addedBy.getAvatarUrl();

        this.removedBy = Optional.empty();
        this.removedById = null;
        this.removedByName = null;
        this.removedByAvatarUrl = null;

        this.ticket = ticket;
    }

    /**
     * Constructor for a ticket member
     *
     * @param id       the id
     * @param memberId the member id
     * @param member   the member
     */
    @SuppressWarnings({ "java:S107" })
    private TicketMember(Ticket ticket, Optional<Long> id, Optional<RestAction<User>> member, String memberId,
            String memberName,
            String memberAvatarUrl, Optional<RestAction<User>> addedBy, String addedById, String addedByName,
            String addedByAvatarUrl, Optional<RestAction<User>> removedBy, String removedById, String removedByName,
            String removedByAvatarUrl) {
        this.id = id;

        this.member = member;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberAvatarUrl = memberAvatarUrl;

        this.addedBy = addedBy;
        this.addedById = addedById;
        this.addedByName = addedByName;
        this.addedByAvatarUrl = addedByAvatarUrl;

        this.removedBy = removedBy;
        this.removedById = removedById;
        this.removedByName = removedByName;
        this.removedByAvatarUrl = removedByAvatarUrl;

        this.ticket = ticket;
    }

    /**
     * Deletes the ticket member
     *
     * @return The result of the deletion
     */
    public SurfFutureResult<Optional<TicketMember>> delete() {
        CompletableFuture<Optional<TicketMember>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketMember>> result = new DiscordFutureResult<>(future);

        Optional<String> ticketIdOptional = getTicket().getTicketId();
        if (ticketIdOptional.isEmpty()) {
            future.complete(Optional.empty());
            return result;
        }
        String ticketId = ticketIdOptional.get();

        if (id.isEmpty()) {
            future.complete(Optional.empty());
            return result;
        }
        long idGet = this.id.get();

        DataApi.getDataInstance().runAsync(() -> {
            String url = String.format(API.TICKET_MEMBER, ticketId, idGet);
            WebRequest request = WebRequest.builder().json(true).parameters(toDeleteParameters()).url(url).build();
            request.executeDelete().thenAccept(response -> {
                if (response.getStatusCode() == 200) {
                    future.complete(Optional.of(this));
                } else {
                    future.complete(Optional.empty());
                }
            }).exceptionally(exception -> {
                Launcher.getLogger().logError("Ticket member could not be deleted: " + exception.getMessage());
                future.completeExceptionally(exception);
                return null;
            });
        });

        return result;
    }

    /**
     * Converts the ticket member to a map of parameters
     *
     * @return The map of parameters
     */
    public Map<String, String> toParameters() {
        Map<String, String> parameters = new HashMap<>();

        if (memberId != null) {
            parameters.put("member_id", memberId);
        }

        if (memberName != null) {
            parameters.put("member_name", memberName);
        }

        if (memberAvatarUrl != null) {
            parameters.put("member_avatar_url", memberAvatarUrl);
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

        return parameters;
    }

    /**
     * Converts the ticket member to a map of delete parameters
     *
     * @return The map of parameters
     */
    public Map<String, String> toDeleteParameters() {
        Map<String, String> parameters = new HashMap<>();

        if (removedById != null) {
            parameters.put("removed_by_id", removedById);
        }

        if (removedByName != null) {
            parameters.put("removed_by_name", removedByName);
        }

        if (removedByAvatarUrl != null) {
            parameters.put("removed_by_avatar_url", removedByAvatarUrl);
        }

        return parameters;
    }

    /**
     * Converts a json object to a ticket member
     *
     * @param ticket     The ticket
     * @param jsonObject The json object
     * @return The ticket member
     */
    @SuppressWarnings({ "java:S3776" })
    public static TicketMember fromJsonObject(Ticket ticket, JsonObject jsonObject) {
        Optional<Long> id = Optional.empty();

        Optional<RestAction<User>> member = Optional.empty();
        String memberId = null;
        String memberName = null;
        String memberAvatarUrl = null;

        Optional<RestAction<User>> addedBy = Optional.empty();
        String addedById = null;
        String addedByName = null;
        String addedByAvatarUrl = null;

        Optional<RestAction<User>> removedBy = Optional.empty();
        String removedById = null;
        String removedByName = null;
        String removedByAvatarUrl = null;

        if (jsonObject.has("id")) {
            JsonElement idElement = jsonObject.get("id");
            if (!idElement.isJsonNull()) {
                id = Optional.of(idElement.getAsLong());
            }
        }

        if (jsonObject.has("member_id")) {
            JsonElement memberIdElement = jsonObject.get("member_id");
            if (!memberIdElement.isJsonNull()) {
                memberId = memberIdElement.getAsString();

                if (memberId != null) {
                    member = Optional
                            .ofNullable(DiscordBot.getInstance().getJda().retrieveUserById(memberId));
                }
            }
        }

        if (jsonObject.has("added_by_id") && jsonObject.get("added_by_id") != null
                && !jsonObject.get("added_by_id").isJsonNull()) {
            addedById = jsonObject.get("added_by_id").getAsString();

            if (addedById != null) {
                addedBy = Optional
                        .ofNullable(DiscordBot.getInstance().getJda().retrieveUserById(addedById));
            }
        }

        if (jsonObject.has("member_name") && jsonObject.get("member_name") != null
                && !jsonObject.get("member_name").isJsonNull()) {
            memberName = jsonObject.get("member_name").getAsString();
        }

        if (jsonObject.has("member_avatar_url") && jsonObject.get("member_avatar_url") != null
                && !jsonObject.get("member_avatar_url").isJsonNull()) {
            memberAvatarUrl = jsonObject.get("member_avatar_url").getAsString();
        }

        if (jsonObject.has("added_by_name") && jsonObject.get("added_by_name") != null
                && !jsonObject.get("added_by_name").isJsonNull()) {
            addedByName = jsonObject.get("added_by_name").getAsString();
        }

        if (jsonObject.has("added_by_avatar_url") && jsonObject.get("added_by_avatar_url") != null
                && !jsonObject.get("added_by_avatar_url").isJsonNull()) {
            addedByAvatarUrl = jsonObject.get("added_by_avatar_url").getAsString();
        }

        if (jsonObject.has("removed_by_id") && jsonObject.get("removed_by_id") != null
                && !jsonObject.get("removed_by_id").isJsonNull()) {
            removedById = jsonObject.get("removed_by_id").getAsString();

            if (removedById != null) {
                removedBy = Optional
                        .ofNullable(DiscordBot.getInstance().getJda().retrieveUserById(removedById));
            }
        }

        if (jsonObject.has("removed_by_name") && jsonObject.get("removed_by_name") != null
                && !jsonObject.get("removed_by_name").isJsonNull()) {
            removedByName = jsonObject.get("removed_by_name").getAsString();
        }

        if (jsonObject.has("removed_by_avatar_url") && jsonObject.get("removed_by_avatar_url") != null
                && !jsonObject.get("removed_by_avatar_url").isJsonNull()) {
            removedByAvatarUrl = jsonObject.get("removed_by_avatar_url").getAsString();
        }

        return new TicketMember(ticket, id, member, memberId, memberName, memberAvatarUrl, addedBy, addedById,
                addedByName, addedByAvatarUrl, removedBy, removedById, removedByName, removedByAvatarUrl);
    }

    /**
     * Creates the ticket member
     *
     * @return The result of the creation
     */
    public SurfFutureResult<Optional<TicketMember>> create() {
        CompletableFuture<Optional<TicketMember>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketMember>> result = new DiscordFutureResult<>(future);

        Optional<String> ticketIdOptional = getTicket().getTicketId();
        if (ticketIdOptional.isEmpty()) {
            future.complete(Optional.empty());
            return result;
        }
        String ticketId = ticketIdOptional.get();

        DataApi.getDataInstance().runAsync(() -> {
            String url = String.format(API.TICKET_MEMBERS, ticketId);
            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters()).build();
            request.executePost().thenAccept(response -> {
                Object responseBody = response.getBody();
                String bodyString = responseBody.toString();

                if (!(response.getStatusCode() == 201 || response.getStatusCode() == 200)) {
                    Launcher.getLogger().logError("Ticket member could not be created: " + bodyString);
                    future.complete(Optional.empty());
                    return;
                }

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                    future.complete(Optional.empty());
                    return;
                }

                JsonObject jsonObject = (JsonObject) bodyElement.get("data");

                TicketMember tempMember = fromJsonObject(ticket, jsonObject);
                id = tempMember.id;

                future.complete(Optional.of(this));
            }).exceptionally(exception -> {
                Launcher.getLogger().logError("Ticket member could not be created: " + exception.getMessage());
                future.completeExceptionally(exception);
                return null;
            });
        });

        return result;
    }

    /**
     * Returns if the ticket member is removed
     *
     * @return If the ticket member is removed
     */
    public boolean isRemoved() {
        return removedBy.isPresent() || removedById != null || removedByName != null || removedByAvatarUrl != null;
    }

    /**
     * @return the id
     */
    public Optional<Long> getId() {
        return id;
    }

    /**
     * @return the member
     */
    public Optional<RestAction<User>> getMember() {
        return member;
    }

    /**
     * @return the memberId
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * @return the ticket
     */
    public Ticket getTicket() {
        return ticket;
    }

    /**
     * @return the addedBy
     */
    public Optional<RestAction<User>> getAddedBy() {
        return addedBy;
    }

    /**
     * @return the addedById
     */
    public String getAddedById() {
        return addedById;
    }

    /**
     * @return the addedByAvatarUrl
     */
    public String getAddedByAvatarUrl() {
        return addedByAvatarUrl;
    }

    /**
     * @return the addedByName
     */
    public String getAddedByName() {
        return addedByName;
    }

    /**
     * @return the memberAvatarUrl
     */
    public String getMemberAvatarUrl() {
        return memberAvatarUrl;
    }

    /**
     * @return the memberName
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * @return the removedBy
     */
    public Optional<RestAction<User>> getRemovedBy() {
        return removedBy;
    }

    /**
     * @return the removedByAvatarUrl
     */
    public String getRemovedByAvatarUrl() {
        return removedByAvatarUrl;
    }

    /**
     * @return the removedById
     */
    public String getRemovedById() {
        return removedById;
    }

    /**
     * @return the removedByName
     */
    public String getRemovedByName() {
        return removedByName;
    }

    /**
     * @param removedBy the removedBy to set
     */
    public void setRemovedBy(Optional<RestAction<User>> removedBy) {
        this.removedBy = removedBy;
    }

    /**
     * @param removedByAvatarUrl the removedByAvatarUrl to set
     */
    public void setRemovedByAvatarUrl(String removedByAvatarUrl) {
        this.removedByAvatarUrl = removedByAvatarUrl;
    }

    /**
     * @param removedById the removedById to set
     */
    public void setRemovedById(String removedById) {
        this.removedById = removedById;
    }

    /**
     * @param removedByName the removedByName to set
     */
    public void setRemovedByName(String removedByName) {
        this.removedByName = removedByName;
    }

}
