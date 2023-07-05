package dev.slne.discord.ticket.member;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

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

    @SerializedName("id")
    private long id;

    @SerializedName("member_id")
    private String memberId;

    @SerializedName("member_name")
    private String memberName;

    @SerializedName("member_avatar_url")
    private String memberAvatarUrl;

    @SerializedName("added_by_id")
    private String addedById;

    @SerializedName("added_by_name")
    private String addedByName;

    @SerializedName("added_by_avatar_url")
    private String addedByAvatarUrl;

    @SerializedName("removed_by_id")
    private String removedById;

    @SerializedName("removed_by_name")
    private String removedByName;

    @SerializedName("removed_by_avatar_url")
    private String removedByAvatarUrl;

    @SerializedName("ticket_id")
    private long ticketRawId;

    /**
     * Constructor for a ticket member
     *
     * @param member The member of the ticket
     */
    public TicketMember(Ticket ticket, User member, User addedBy) {
        if (member != null) {
            this.memberId = member.getId();
            this.memberName = member.getName();
            this.memberAvatarUrl = member.getAvatarUrl();
        }

        if (addedBy != null) {
            this.addedById = addedBy.getId();
            this.addedByName = addedBy.getName();
            this.addedByAvatarUrl = addedBy.getAvatarUrl();
        }

        this.removedById = null;
        this.removedByName = null;
        this.removedByAvatarUrl = null;

        if (ticket != null) {
            this.ticketRawId = ticket.getId();
        }
    }

    /**
     * Deletes the ticket member
     *
     * @return The result of the deletion
     */
    public SurfFutureResult<TicketMember> delete() {
        CompletableFuture<TicketMember> future = new CompletableFuture<>();
        DiscordFutureResult<TicketMember> result = new DiscordFutureResult<>(future);

        Ticket ticket = getTicket();

        if (ticket == null) {
            future.complete(null);
            return result;
        }

        String ticketId = ticket.getTicketId();

        if (ticketId == null) {
            future.complete(null);
            return result;
        }

        DataApi.getDataInstance().runAsync(() -> {
            String url = String.format(API.TICKET_MEMBER, ticketId, id);
            WebRequest request = WebRequest.builder().json(true).parameters(toDeleteParameters()).url(url).build();
            request.executeDelete().thenAccept(response -> {
                if (response.getStatusCode() == 200) {
                    future.complete(this);
                } else {
                    future.complete(null);
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
     * Returns the ticket member from a json object
     *
     * @return The ticket member
     */
    private TicketMember fromJsonObject(JsonObject jsonObject) {
        GsonConverter gson = new GsonConverter();

        return gson.fromJson(jsonObject.toString(), TicketMember.class);
    }

    /**
     * Creates the ticket member
     *
     * @return The result of the creation
     */
    public SurfFutureResult<TicketMember> create() {
        CompletableFuture<TicketMember> future = new CompletableFuture<>();
        DiscordFutureResult<TicketMember> result = new DiscordFutureResult<>(future);

        Ticket ticket = getTicket();

        if (ticket == null) {
            future.complete(null);
            return result;
        }

        String ticketId = ticket.getTicketId();

        if (ticketId == null) {
            future.complete(null);
            return result;
        }

        DataApi.getDataInstance().runAsync(() -> {
            String url = String.format(API.TICKET_MEMBERS, ticketId);
            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters()).build();
            request.executePost().thenAccept(response -> {
                Object responseBody = response.getBody();
                String bodyString = responseBody.toString();

                if (!(response.getStatusCode() == 201 || response.getStatusCode() == 200)) {
                    Launcher.getLogger().logError("Ticket member could not be created: " + bodyString);
                    future.complete(null);
                    return;
                }

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                    future.complete(null);
                    return;
                }

                JsonObject jsonObject = (JsonObject) bodyElement.get("data");

                TicketMember tempMember = fromJsonObject(jsonObject);
                id = tempMember.id;

                future.complete(this);
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
        return getRemovedBy() != null || removedById != null || removedByName != null || removedByAvatarUrl != null;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the member
     */
    public RestAction<User> getMember() {
        if (memberId == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().retrieveUserById(memberId + "");
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
        if (ticketRawId == 0) {
            return null;
        }

        return DiscordBot.getInstance().getTicketManager().getTicketById(ticketRawId);
    }

    /**
     * @param ticketId the ticketId to set
     */
    public void setTicketRawId(long ticketId) {
        this.ticketRawId = ticketId;
    }

    /**
     * @param addedByAvatarUrl the addedByAvatarUrl to set
     */
    public void setAddedByAvatarUrl(String addedByAvatarUrl) {
        this.addedByAvatarUrl = addedByAvatarUrl;
    }

    /**
     * @param addedById the addedById to set
     */
    public void setAddedById(String addedById) {
        this.addedById = addedById;
    }

    /**
     * @param addedByName the addedByName to set
     */
    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @param memberAvatarUrl the memberAvatarUrl to set
     */
    public void setMemberAvatarUrl(String memberAvatarUrl) {
        this.memberAvatarUrl = memberAvatarUrl;
    }

    /**
     * @param memberId the memberId to set
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    /**
     * @param memberName the memberName to set
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    /**
     * @return the ticketId
     */
    public long getTicketRawId() {
        return ticketRawId;
    }

    /**
     * @return the addedBy
     */
    public RestAction<User> getAddedBy() {
        if (addedById == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().retrieveUserById(addedById + "");
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
    public RestAction<User> getRemovedBy() {
        if (removedById == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().retrieveUserById(removedById + "");
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
