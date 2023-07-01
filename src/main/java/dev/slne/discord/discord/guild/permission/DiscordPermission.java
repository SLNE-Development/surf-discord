package dev.slne.discord.discord.guild.permission;

import net.dv8tion.jda.api.Permission;

public enum DiscordPermission {

    ALL("*", true, true, null),
    ALL_DISCORD("*", false, true, null),
    ALL_ROLE("*", true, false, null),

    // General
    VIEW_CHANNEL("VIEW_CHANNEL", false, true, Permission.VIEW_CHANNEL),
    MANAGE_CHANNEL("MANAGE_CHANNEL", false, true, Permission.MANAGE_CHANNEL),

    // Threads
    CREATE_PUBLIC_THREADS("CREATE_PUBLIC_THREADS", false, true, Permission.CREATE_PUBLIC_THREADS),
    CREATE_PRIVATE_THREADS("CREATE_PRIVATE_THREADS", false, true, Permission.CREATE_PRIVATE_THREADS),
    SEND_MESSAGES_IN_THREADS("SEND_MESSAGES_IN_THREADS", false, true, Permission.MESSAGE_SEND_IN_THREADS),
    MANAGE_THREADS("MANAGE_THREADS", false, true, Permission.MANAGE_THREADS),

    // Messages
    SEND_MESSAGES("SEND_MESSAGES", false, true, Permission.MESSAGE_SEND),
    EMBED_LINKS("EMBED_LINKS", false, true, Permission.MESSAGE_EMBED_LINKS),
    ATTACH_FILES("ATTACH_FILES", false, true, Permission.MESSAGE_ATTACH_FILES),
    ADD_REACTIONS("ADD_REACTIONS", false, true, Permission.MESSAGE_ADD_REACTION),
    USE_EXTERNAL_EMOJIS("USE_EXTERNAL_EMOJIS", false, true, Permission.MESSAGE_EXT_EMOJI),
    USE_EXTERNAL_STICKERS("USE_EXTERNAL_STICKERS", false, true, Permission.MESSAGE_EXT_STICKER),
    MANAGE_MESSAGES("MANAGE_MESSAGES", false, true, Permission.MESSAGE_MANAGE),
    READ_MESSAGE_HISTORY("READ_MESSAGE_HISTORY", false, true, Permission.MESSAGE_HISTORY),
    USE_APPLICATION_COMMANDS("USE_APPLICATION_COMMANDS", false, true, Permission.USE_APPLICATION_COMMANDS),

    // Webhooks
    MANAGE_WEBHOOKS("MANAGE_WEBHOOKS", false, true, Permission.MANAGE_WEBHOOKS),

    // Tickets
    VIEW_ALL_TICKETS("VIEW_ALL_TICKETS", true, false, null),
    VIEW_WHITELIST_TICKETS("VIEW_WHITELIST_TICKETS", true, false, null),
    VIEW_SERVER_SUPPORT_TICKETS("VIEW_SERVER_SUPPORT_TICKETS", true, false, null),
    VIEW_DISCORD_SUPPORT_TICKETS("VIEW_DISCORD_SUPPORT_TICKETS", true, false, null),
    VIEW_BUGREPORT_TICKETS("VIEW_BUGREPORT_TICKETS", true, false, null),

    // Commands
    USE_COMMAND_NO_INTEREST("USE_COMMAND_NO_INTEREST", true, false, null),
    USE_COMMAND_TWITCH_CONNECT("USE_COMMAND_TWITCH_CONNECT", true, false, null),
    USE_COMMAND_WHITELIST("USE_COMMAND_WHITELIST", true, false, null),
    USE_COMMAND_WHITELISTED("USE_COMMAND_WHITELISTED", true, false, null),
    USE_COMMAND_WHITELIST_QUERY("USE_COMMAND_WHITELIST_QUERY", true, false, null),
    USE_COMMAND_WHITELIST_ROLE("USE_COMMAND_WHITELIST_ROLE", true, false, null),
    USE_COMMAND_TICKET_ADD_USER("USE_COMMAND_TICKET_ADD_USER", true, false, null),
    USE_COMMAND_TICKET_REMOVE_USER("USE_COMMAND_TICKET_REMOVE_USER", true, false, null),
    USE_COMMAND_TICKET_CLOSE("USE_COMMAND_TICKET_CLOSE", true, false, null),
    USE_COMMAND_TICKET_BUTTONS("USE_COMMAND_TICKET_BUTTONS", true, false, null),
    USE_COMMAND_TICKET_INFO("USE_COMMAND_TICKET_INFO", true, false, null),
    USE_COMMAND_TICKET_BAN("USE_COMMAND_TICKET_BAN", true, false, null),
    USE_COMMAND_TICKET_UNBAN("USE_COMMAND_TICKET_UNBAN", true, false, null),
    USE_COMMAND_TICKET_BANLIST("USE_COMMAND_TICKET_BANLIST", true, false, null),
    USE_COMMAND_TICKET_BANINFO("USE_COMMAND_TICKET_BANINFO", true, false, null),
    USE_COMMAND_TICKET_STATISTIC("USE_COMMAND_TICKET_STATISTIC", true, false, null),
    USE_COMMAND_REACTION_ROLE_TEXT("USE_COMMAND_REACTION_ROLE_TEXT", true, false, null);

    private String name;
    private boolean rolePermission;
    private boolean dcPermission;
    private Permission permission;

    /**
     * Returns the name
     *
     * @param name
     */
    private DiscordPermission(String name, boolean rolePermission, boolean dcPermission, Permission permission) {
        this.name = name;
        this.rolePermission = rolePermission;
        this.dcPermission = dcPermission;
        this.permission = permission;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the permission
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     * @return the dcPermission
     */
    public boolean isDiscordPermission() {
        return dcPermission;
    }

    /**
     * @return the rolePermission
     */
    public boolean isRolePermission() {
        return rolePermission;
    }
}
