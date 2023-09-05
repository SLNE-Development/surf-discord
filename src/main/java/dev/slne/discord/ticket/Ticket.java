package dev.slne.discord.ticket;

import com.google.gson.annotations.SerializedName;
import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.Times;
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.discord.guild.role.DiscordRole;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.message.TicketMessage;
import dev.slne.discord.ticket.result.TicketCloseResult;
import dev.slne.discord.ticket.result.TicketCreateResult;
import dev.slne.discord.whitelist.Whitelist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Ticket {

    @SerializedName("id")
    private long id;

    @SerializedName("ticket_id")
    private String ticketId;

    @SerializedName("opened_at")
    private LocalDateTime openedAt;

    @SerializedName("guild_id")
    private String guildId;

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("type")
    private String ticketTypeString;

    @SerializedName("author_id")
    private String ticketAuthorId;

    @SerializedName("author_name")
    private String ticketAuthorName;

    @SerializedName("author_avatar_url")
    private String ticketAuthorAvatarUrl;

    @SerializedName("closed_by_id")
    private String closedById;

    @SerializedName("closed_reason")
    private String closedReason;

    @SerializedName("closed_at")
    private LocalDateTime closedAt;

    @SerializedName("messages")
    private List<TicketMessage> messages;

    @SerializedName("members")
    private List<TicketMember> members;
    private List<TicketMember> removedMembers;

    @SerializedName("webhook_id")
    private String webhookId;

    @SerializedName("webhook_name")
    private String webhookName;

    @SerializedName("webhook_avatar_url")
    private String webhookUrl;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    /**
     * Constructor for a ticket
     *
     * @param guild        The guild the ticket is created in
     * @param ticketAuthor The author of the ticket
     * @param ticketType   The type of the ticket
     */
    public Ticket(Guild guild, User ticketAuthor, TicketType ticketType) {
        this.openedAt = Times.now();

        if (guild != null) {
            this.guildId = guild.getId();
        }

        if (ticketType != null) {
            this.ticketTypeString = ticketType.name();
        }

        if (ticketAuthor != null) {
            this.ticketAuthorName = ticketAuthor.getName();
            this.ticketAuthorId = ticketAuthor.getId();
            this.ticketAuthorAvatarUrl = ticketAuthor.getAvatarUrl();
        }

        this.messages = new ArrayList<>();
        this.members = new ArrayList<>();
        this.removedMembers = new ArrayList<>();
    }

    /**
     * After the ticket is opened
     */
    public CompletableFuture<Void> afterOpen() {
        // Implemented by subclasses
        return CompletableFuture.completedFuture(null);
    }

    /**
     * After the ticket is closed
     */
    public void afterClose() {
        // Mainly mplemented by subclasses
    }

    /**
     * Adds a ticket message to the ticket
     *
     * @param ticketMessage The ticket message
     *
     * @return The result of the ticket message adding
     */
    public CompletableFuture<TicketMessage> addTicketMessage(TicketMessage ticketMessage) {
        CompletableFuture<TicketMessage> future = new CompletableFuture<>();

        ticketMessage.create().thenAcceptAsync(newTicketMessage -> {
            if (newTicketMessage == null) {
                future.complete(null);
                return;
            }

            addRawTicketMessage(ticketMessage);
            future.complete(ticketMessage);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });

        return future;
    }

    /**
     * Adds a ticket member to the ticket
     *
     * @param ticketMember The ticket member
     *
     * @return The result of the ticket member adding
     */
    public CompletableFuture<TicketMember> addTicketMember(TicketMember ticketMember) {
        CompletableFuture<TicketMember> future = new CompletableFuture<>();
        RestAction<User> userRest = ticketMember.getMember();

        if (userRest == null) {
            future.complete(null);
            return future;
        }

        userRest.queue(user -> {
            if (user == null || memberExists(user)) {
                future.complete(null);
                return;
            }

            ticketMember.create().thenAcceptAsync(newTicketMember -> {
                if (newTicketMember == null) {
                    future.complete(null);
                    return;
                }

                addRawTicketMember(ticketMember);
                future.complete(ticketMember);
            }).exceptionally(throwable -> {
                future.completeExceptionally(throwable);
                return null;
            });
        });

        return future;
    }

    /**
     * Removes a ticket member from the ticket
     *
     * @param ticketMember The ticket member
     *
     * @return The result of the ticket member removing
     */
    public CompletableFuture<TicketMember> removeTicketMember(TicketMember ticketMember) {
        CompletableFuture<TicketMember> future = new CompletableFuture<>();

        ticketMember.delete().thenAcceptAsync(deletedTicketMember -> {
            if (deletedTicketMember == null) {
                future.complete(null);
                return;
            }

            removeRawTicketMember(ticketMember);
            future.complete(ticketMember);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });

        return future;
    }

    /**
     * Get the embed for the ticket closed message
     *
     * @return The embed for the ticket closed message
     */
    public CompletableFuture<MessageEmbed> getTicketClosedEmbed() {
        CompletableFuture<MessageEmbed> future = new CompletableFuture<>();

        TicketChannel.getTicketName(this).thenAcceptAsync(ticketName -> {
            if (ticketName == null) {
                future.complete(null);
                return;
            }

            RestAction<User> closedByRest = getClosedBy();

            getTicketAuthor().queue(author -> {
                if (closedByRest != null) {
                    closedByRest.queue(closedByUser -> {
                        MessageEmbed embed = formEmbed(author, closedByUser, ticketName);
                        future.complete(embed);
                    });

                    return;
                }

                MessageEmbed embed = formEmbed(author, null, ticketName);
                future.complete(embed);
            }, future::completeExceptionally);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });

        return future;
    }

    /**
     * Forms the acutal embed
     *
     * @param author     The author
     * @param closedBy   The user that closed the ticket
     * @param ticketName The name of the ticket
     *
     * @return The embed
     */
    private MessageEmbed formEmbed(User author, User closedBy, String ticketName) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Ticket \"" + ticketName + "\" geschlossen");

        String reason = getClosedReason();

        if (reason == null) {
            reason = "Kein Grund angegeben.";
        }

        String description = "Ein Ticket wurde ";
        if (closedBy != null) {
            description += "von " + closedBy.getAsMention() + " ";
        }
        description += "geschlossen.\n\nGrund:\n" + reason;
        embedBuilder.setDescription(description);

        embedBuilder.setColor(Color.decode("#ff6600"));

        LocalDateTime openedAtDateTime = getCreatedAt();
        LocalDateTime closedAtDateTime = getClosedAt();

        if (closedAtDateTime == null) {
            closedAtDateTime = Times.now();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        embedBuilder.addField("Ticket-ID", getTicketId(), true);
        embedBuilder.addField("Ticket-Type", getTicketTypeString(), true);
        embedBuilder.addField("Ticket-Author", author.getAsMention(), true);

        if (openedAtDateTime != null) {
            embedBuilder.addField("Ticket-Eröffnungszeit", formatter.format(openedAtDateTime), true);
        }

        embedBuilder.addField("Ticket-Schließzeit", formatter.format(closedAtDateTime), true);

        if (openedAtDateTime != null) {
            long[] tempDifferences = toTempUnits(openedAtDateTime, closedAtDateTime);
            long days = tempDifferences[2];
            long hours = tempDifferences[3];
            long minutes = tempDifferences[4];
            long seconds = tempDifferences[5];

            String differenceString = String.format("%d Tage, %d Stunden, %d Minuten, %d Sekunden", days,
                    hours,
                    minutes, seconds);
            embedBuilder.addField("Ticket-Dauer", differenceString, true);
        }

        return embedBuilder.build();
    }

    /**
     * Send the ticket closed messages
     */
    public CompletableFuture<Void> sendTicketClosedMessages() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        getTicketClosedEmbed().thenAcceptAsync(embed -> {
            if (embed == null) {
                future.complete(null);
                return;
            }

            getTicketAuthor().queue(author -> {
                Guild guild = getGuild();

                if (guild == null) {
                    future.complete(null);
                    return;
                }

                DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

                if (discordGuild == null) {
                    future.complete(null);
                    return;
                }

                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (TicketMember member : members) {
                    CompletableFuture<Void> memberFuture = new CompletableFuture<>();
                    futures.add(memberFuture);

                    if (member.isRemoved()) {
                        memberFuture.complete(null);
                        continue;
                    }

                    RestAction<User> memberUserRest = member.getMember();
                    if (memberUserRest != null) {
                        memberUserRest.queue(memberUser -> {
                            if (memberUser == null) {
                                memberFuture.complete(null);
                                return;
                            }

                            if (memberUser.equals(DiscordBot.getInstance().getJda().getSelfUser())) {
                                memberFuture.complete(null);
                                return;
                            }

                            discordGuild.isAdminUser(author).thenAcceptAsync(isAdminUser -> {
                                boolean memberIsAuthor = memberUser.equals(author);

                                if (memberIsAuthor || !isAdminUser) {
                                    memberUser.openPrivateChannel()
                                            .queue(privateChannel -> privateChannel.sendMessageEmbeds(embed)
                                                            .queue(v -> {
                                                                memberFuture.complete(null);
                                                            }, failure -> {
                                                                if (failure instanceof ErrorResponseException errorResponseException
                                                                        && errorResponseException.getErrorCode() == 50007) {
                                                                    memberFuture.complete(null);
                                                                    return;
                                                                }

                                                                DataApi.getDataInstance().logError(getClass(),
                                                                        "Error while opening ticket closed message: ",
                                                                        failure);
                                                                memberFuture.completeExceptionally(failure);
                                                            }),
                                                    failure -> {
                                                        if (failure instanceof ErrorResponseException errorResponseException
                                                                && errorResponseException.getErrorCode() == 50007) {
                                                            memberFuture.complete(null);
                                                            return;
                                                        }

                                                        DataApi.getDataInstance().logError(getClass(),
                                                                "Error while opening ticket closed message: ",
                                                                failure);
                                                        memberFuture.completeExceptionally(failure);
                                                    });
                                } else {
                                    memberFuture.complete(null);
                                }
                            }).exceptionally(throwable -> {
                                DataApi.getDataInstance().logError(getClass(),
                                        "Error while opening ticket closed message: ", throwable);
                                memberFuture.completeExceptionally(throwable);
                                return null;
                            });
                        });
                    }
                }

                CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                        .thenAcceptAsync(v -> future.complete(null));
            });
        }).exceptionally(throwable -> {
            DataApi.getDataInstance().logError(getClass(), "Error while opening ticket closed message: ", throwable);
            future.completeExceptionally(throwable);
            return null;
        });

        return future;
    }

    /**
     * Get the time difference between two dates
     *
     * @param start The start date
     * @param end   The end date
     *
     * @return The time difference between two dates
     */
    private long[] toTempUnits(LocalDateTime start, LocalDateTime end) {
        LocalDateTime tempDateTime = LocalDateTime.from(start);

        long years = tempDateTime.until(end, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until(end, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(end, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        long hours = tempDateTime.until(end, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(end, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long seconds = tempDateTime.until(end, ChronoUnit.SECONDS);

        return new long[] { years, months, days, hours, minutes, seconds };
    }

    /**
     * Adds a raw ticket member to the ticket
     *
     * @param ticketMember The ticket member
     */
    public void addRawTicketMember(TicketMember ticketMember) {
        members.add(ticketMember);
    }

    /**
     * Removes a raw ticket member from the ticket
     *
     * @param ticketMember The ticket member
     */
    public void removeRawTicketMember(TicketMember ticketMember) {
        members.remove(ticketMember);
        removedMembers.add(ticketMember);
    }

    /**
     * Adds a raw ticket message to the ticket
     *
     * @param ticketMessage The ticket message
     */
    public void addRawTicketMessage(TicketMessage ticketMessage) {
        messages.add(ticketMessage);
    }

    /**
     * Check if the member exists
     *
     * @param user The user to check
     *
     * @return If the member exists
     */
    private boolean memberExists(User user) {
        return members.stream().anyMatch(member -> member.getMemberId().equals(user.getId()) && !member.isRemoved());
    }

    /**
     * Open the ticket
     *
     * @param runnable The runnable to run after the ticket is opened
     *
     * @return The result of the ticket opening
     */
    @SuppressWarnings({ "java:S3776", "java:S1602" })
    private CompletableFuture<TicketCreateResult> open(Runnable runnable) {
        CompletableFuture<TicketCreateResult> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> TicketChannel.getTicketName(this).thenAcceptAsync(ticketName -> {
            if (ticketName == null) {
                future.complete(TicketCreateResult.ERROR);
                return;
            }

            Guild guild = getGuild();
            if (guild == null) {
                future.complete(TicketCreateResult.GUILD_NOT_FOUND);
                return;
            }

            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

            if (discordGuild == null) {
                future.complete(TicketCreateResult.GUILD_NOT_FOUND);
                return;
            }

            String categoryId = discordGuild.getCategoryId();
            Category channelCategory = guild.getCategoryById(categoryId);

            if (channelCategory == null) {
                future.complete(TicketCreateResult.CATEGORY_NOT_FOUND);
                return;
            }

            getTicketAuthor().queue(
                    author -> TicketChannel.checkTicketExists(ticketName, channelCategory, getTicketType(), author)
                            .thenAcceptAsync(ticketExistsBoolean -> {
                                boolean ticketExists = ticketExistsBoolean;

                                if (ticketExists) {
                                    future.complete(TicketCreateResult.ALREADY_EXISTS);
                                    return;
                                }

                                TicketRepository.createTicket(this).thenAcceptAsync(ticketCreateResult -> {
                                    if (ticketCreateResult == null) {
                                        future.complete(TicketCreateResult.ERROR);
                                        return;
                                    }

                                    DiscordBot.getInstance().getTicketManager().addTicket(this);

                                    TicketChannel.createTicketChannel(this, ticketName, channelCategory)
                                            .thenAcceptAsync(ticketChannelCreateResult -> {
                                                if (ticketChannelCreateResult == null) {
                                                    future.complete(TicketCreateResult.ERROR);
                                                    return;
                                                }

                                                if (ticketChannelCreateResult != TicketCreateResult.SUCCESS) {
                                                    future.complete(ticketChannelCreateResult);
                                                    return;
                                                }

                                                afterOpen().thenAcceptAsync(v -> {
                                                    runnable.run();
                                                    future.complete(TicketCreateResult.SUCCESS);
                                                }).exceptionally(exception -> {
                                                    future.completeExceptionally(exception);
                                                    return null;
                                                });
                                            }).exceptionally(exception -> {
                                                future.completeExceptionally(exception);
                                                return null;
                                            });
                                }).exceptionally(exception -> {
                                    future.completeExceptionally(exception);
                                    return null;
                                });
                            }).exceptionally(exception -> {
                                future.completeExceptionally(exception);
                                return null;
                            }));
        }).exceptionally(exception -> {
            future.completeExceptionally(exception);
            return null;
        }));

        return future;
    }

    /**
     * Opens the ticket from the button
     *
     * @return The result of the ticket opening
     */
    public CompletableFuture<TicketCreateResult> openFromButton() {
        return this.open(() -> {
        });
    }

    /**
     * Open the ticket channel from pusher
     *
     * @return The result of the ticket opening
     */
    public CompletableFuture<TicketCreateResult> openFromPusher() {
        return this.open(this::printAllPreviousMessages);
    }

    /**
     * Print all previous messages
     */
    private void printAllPreviousMessages() {
        for (TicketMessage message : messages) {
            message.printMessage();
        }
    }

    /**
     * Close the ticket channel
     *
     * @param user   The user that closed the ticket
     * @param reason The reason the ticket was closed
     *
     * @return The result of the ticket closing
     */
    public CompletableFuture<TicketCloseResult> close(User user, String reason) {
        CompletableFuture<TicketCloseResult> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            TextChannel channel = getChannel();

            if (channel == null) {
                future.complete(TicketCloseResult.TICKET_NOT_FOUND);
                return;
            }

            this.closedById = user.getId();
            this.closedReason = reason;

            TicketRepository.closeTicket(this).thenAcceptAsync(newTicket -> {
                if (newTicket == null) {
                    future.complete(TicketCloseResult.TICKET_REPOSITORY_ERROR);
                    return;
                }

                TicketChannel.deleteTicketChannel(this).thenAcceptAsync(v -> {
                    future.complete(TicketCloseResult.SUCCESS);

                    sendTicketClosedMessages();
                    DiscordBot.getInstance().getTicketManager().removeTicket(this);
                    afterClose();
                }).exceptionally(throwable -> {
                    future.complete(TicketCloseResult.TICKET_CHANNEL_NOT_CLOSABLE);
                    DataApi.getDataInstance().logError(getClass(), "Error while closing ticket", throwable);

                    return null;
                });
            }).exceptionally(throwable -> {
                future.complete(TicketCloseResult.TICKET_REPOSITORY_ERROR);

                DataApi.getDataInstance().logError(getClass(), "Error while closing ticket", throwable);
                return null;
            });
        });

        return future;
    }

    /**
     * Adds a role to the ticket channel
     *
     * @param role The role
     *
     * @return The permission override
     */
    public CompletableFuture<PermissionOverride> addTicketRole(Role role) {
        TextChannel channel = getChannel();

        if (channel == null) {
            return CompletableFuture.completedFuture(null);
        }

        DiscordRole discordRole = DiscordGuilds.getGuild(getGuild()).getDiscordRoleByRole(role);
        List<Permission> allowedPermissions = discordRole.getDiscordAllowedPermissions();

        PermissionOverrideAction permissionOverrideAction = channel.upsertPermissionOverride(role);
        permissionOverrideAction = permissionOverrideAction.resetAllow();
        permissionOverrideAction = permissionOverrideAction.resetDeny();
        permissionOverrideAction = permissionOverrideAction.setAllowed(allowedPermissions);

        return permissionOverrideAction.submit();
    }

    /**
     * Prints the wl query embeds
     */
    public void printWlQueryEmbeds() {
        getTicketAuthor().queue(ticketAuthor -> {
            if (ticketAuthor == null) {
                return;
            }

            Whitelist.getWhitelists(null, ticketAuthorId, null).thenAcceptAsync(
                            whitelists -> printWlQuery(getChannel(), "\"" + ticketAuthor.getName() + "\"", whitelists))
                    .exceptionally(throwable -> {
                        DataApi.getDataInstance()
                                .logError(getClass(), "Error while printing wl query embeds", throwable);
                        return null;
                    });
        });
    }

    /**
     * Prints a wlquery request.
     *
     * @param channel    The channel.
     * @param title      The title.
     * @param whitelists The whitelists.
     */
    public void printWlQuery(TextChannel channel, String title, List<Whitelist> whitelists) {
        title = title.replace("\"", "");

        String finalTitle = title;
        channel.sendMessage("WlQuery für: \"" + title + "\"").queue(v -> {
            if (whitelists != null) {
                for (Whitelist whitelist : whitelists) {
                    Whitelist.getWhitelistQueryEmbed(whitelist).thenAcceptAsync(embed -> {
                        if (embed != null) {
                            channel.sendMessageEmbeds(embed).queue();
                        }
                    }).exceptionally(throwable -> {
                        DataApi.getDataInstance()
                                .logError(getClass(), "Error while printing wl query embeds", throwable);
                        return null;
                    });
                }

                if (whitelists.isEmpty()) {
                    channel.sendMessage("Es wurden keine Whitelist Einträge für " + finalTitle + " gefunden.").queue();
                }
            } else {
                channel.sendMessage("Es wurden keine Whitelist Einträge für " + finalTitle + " gefunden.").queue();
            }
        });
    }

    /**
     * Returns the ticket message by the message
     *
     * @param message The message
     *
     * @return The ticket message
     */
    public TicketMessage getTicketMessage(Message message) {
        return messages.stream().filter(ticketMessage -> ticketMessage.getMessageId().equals(message.getId()))
                .findFirst().orElse(null);
    }

    /**
     * Returns the ticket message by the message id
     *
     * @param messageId The message id
     *
     * @return The ticket message
     */
    public TicketMessage getTicketMessage(String messageId) {
        return messages.stream().filter(ticketMessage -> ticketMessage.getMessageId().equals(messageId)).findFirst()
                .orElse(null);
    }

    /**
     * Returns the ticket member by the member
     *
     * @param user The member
     *
     * @return The ticket member
     */
    public TicketMember getTicketMember(User user) {
        return members.stream().filter(ticketMember -> ticketMember.getMemberId().equals(user.getId())).findFirst()
                .orElse(null);
    }

    /**
     * Returns the active ticket member by the member
     *
     * @param user The member
     *
     * @return The active ticket member
     */
    public TicketMember getActiveTicketMember(User user) {
        return members.stream()
                .filter(ticketMember -> ticketMember.getMemberId().equals(user.getId()) && !ticketMember.isRemoved())
                .findFirst().orElse(null);
    }

    /**
     * Returns the ticket member by the member id
     *
     * @param userId The member id
     *
     * @return The ticket member
     */
    public TicketMember getTicketMember(String userId) {
        return members.stream().filter(ticketMember -> ticketMember.getMemberId().equals(userId)).findFirst()
                .orElse(null);
    }

    /**
     * Get the ticket id
     *
     * @return The ticket id
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * @param ticketId the ticketId to set
     */
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * @return the ticketAuthor
     */
    public RestAction<User> getTicketAuthor() {
        if (ticketAuthorId == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().retrieveUserById(ticketAuthorId);
    }

    /**
     * Get the id of the author of the ticket
     *
     * @return The id of the author of the ticket
     */
    public String getTicketAuthorId() {
        return ticketAuthorId;
    }

    /**
     * @param ticketAuthorId the ticketAuthorId to set
     */
    public void setTicketAuthorId(String ticketAuthorId) {
        this.ticketAuthorId = ticketAuthorId;
    }

    /**
     * Get the type of the ticket
     *
     * @return The type of the ticket
     */
    public TicketType getTicketType() {
        return TicketType.valueOf(ticketTypeString);
    }

    /**
     * Get the type of the ticket as a string
     *
     * @return The type of the ticket as a string
     */
    public String getTicketTypeString() {
        return ticketTypeString;
    }

    /**
     * @param ticketTypeString the ticketTypeString to set
     */
    public void setTicketTypeString(String ticketTypeString) {
        this.ticketTypeString = ticketTypeString;
    }

    /**
     * Get the opened at date of the ticket
     *
     * @return The opened at date of the ticket
     */
    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    /**
     * @param openedAt the openedAt to set
     */
    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    /**
     * Get the id of the guild the ticket is created in
     *
     * @return The id of the guild the ticket is created in
     */
    public String getGuildId() {
        return guildId;
    }

    /**
     * @param guildId the guildId to set
     */
    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    /**
     * Get the guild the ticket is created in
     *
     * @return The guild the ticket is created in
     */
    public Guild getGuild() {
        if (guildId == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().getGuildById(guildId);
    }

    /**
     * Get the id of the channel the ticket is created in
     *
     * @return The id of the channel the ticket is created in
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * @param channelId the channelId to set
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     * Get the channel the ticket is created in
     *
     * @return The channel the ticket is created in
     */
    public TextChannel getChannel() {
        if (channelId == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().getTextChannelById(channelId);
    }

    /**
     * Get the id of the user that closed the ticket
     *
     * @return The id of the user that closed the ticket
     */
    public String getClosedById() {
        return closedById;
    }

    /**
     * @param closedById the closedById to set
     */
    public void setClosedById(String closedById) {
        this.closedById = closedById;
    }

    /**
     * @return the closedBy
     */
    public RestAction<User> getClosedBy() {
        if (closedById == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().retrieveUserById(closedById);
    }

    /**
     * Get the reason the ticket was closed
     *
     * @return The reason the ticket was closed
     */
    public String getClosedReason() {
        return closedReason;
    }

    /**
     * @param closedReason the closedReason to set
     */
    public void setClosedReason(String closedReason) {
        this.closedReason = closedReason;
    }

    /**
     * Get the date the ticket was closed
     *
     * @return The date the ticket was closed
     */
    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    /**
     * @param closedAt the closedAt to set
     */
    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    /**
     * Returns the id of the ticket
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the messages
     */
    public List<TicketMessage> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(List<TicketMessage> messages) {
        this.messages = messages;
    }

    /**
     * @return the members
     */
    public List<TicketMember> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<TicketMember> members) {
        this.members = members;
    }

    /**
     * @return the ticketAuthorAvatarUrl
     */
    public String getTicketAuthorAvatarUrl() {
        return ticketAuthorAvatarUrl;
    }

    /**
     * @param ticketAuthorAvatarUrl the ticketAuthorAvatarUrl to set
     */
    public void setTicketAuthorAvatarUrl(String ticketAuthorAvatarUrl) {
        this.ticketAuthorAvatarUrl = ticketAuthorAvatarUrl;
    }

    /**
     * @return the ticketAuthorName
     */
    public String getTicketAuthorName() {
        return ticketAuthorName;
    }

    /**
     * @param ticketAuthorName the ticketAuthorName to set
     */
    public void setTicketAuthorName(String ticketAuthorName) {
        this.ticketAuthorName = ticketAuthorName;
    }

    /**
     * @return the removedMembers
     */
    public List<TicketMember> getRemovedMembers() {
        return removedMembers;
    }

    /**
     * @param removedMembers the removedMembers to set
     */
    public void setRemovedMembers(List<TicketMember> removedMembers) {
        this.removedMembers = removedMembers;
    }

    /**
     * @return the webhook
     */
    public RestAction<Webhook> getWebhook() {
        return DiscordBot.getInstance().getJda().retrieveWebhookById(webhookId);
    }

    /**
     * @return the webhookId
     */
    public String getWebhookId() {
        return webhookId;
    }

    /**
     * @param webhookId the webhookId to set
     */
    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    /**
     * @return the webhookName
     */
    public String getWebhookName() {
        return webhookName;
    }

    /**
     * @param webhookName the webhookName to set
     */
    public void setWebhookName(String webhookName) {
        this.webhookName = webhookName;
    }

    /**
     * @return the webhookUrl
     */
    public String getWebhookUrl() {
        return webhookUrl;
    }

    /**
     * @param webhookUrl the webhookUrl to set
     */
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    /**
     * @return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
