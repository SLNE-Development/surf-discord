package dev.slne.surf.discord.faq

import dev.slne.surf.discord.messages.translatable

enum class Faq(
    val id: String,
    val question: String,
    val answer: String,
    val attachmentPath: String? = null
) {
    CONNECT_TWITCH(
        "connect-twitch",
        translatable("faq.command.questions.connect-twitch-with-discord.question"),
        translatable("faq.command.questions.connect-twitch-with-discord.answer")
    ),
    BANNED(
        "banned",
        translatable("faq.command.questions.banned.question"),
        translatable("faq.command.questions.banned.answer")
    ),
    NEXT_EVENT(
        "next-event",
        translatable("faq.command.questions.event.question"),
        translatable("faq.command.questions.event.answer")
    ),
    HOW_TO_OPEN_TICKET(
        "how-to-open-ticket",
        translatable("faq.command.questions.open-ticket.question"),
        translatable("faq.command.questions.open-ticket.answer")
    ),
    RULEBOOK(
        "rulebook",
        translatable("faq.command.questions.rulebook.question"),
        translatable("faq.command.questions.rulebook.answer")
    ),
    SERVER_MODPACK(
        "server-modpack",
        translatable("faq.command.questions.server-modpack.question"),
        translatable("faq.command.questions.server-modpack.answer")
    ),
    RESOURCEPACK_ISSUES(
        "problem-resourcepack",
        translatable("faq.command.questions.problem-ressourcepack.question"),
        translatable("faq.command.questions.problem-ressourcepack.answer")
    ),
    NRC_VOICE_CHAT_ISSUES(
        "problem-nrc-voice-chat",
        translatable("faq.command.questions.problem-nrc-voice-chat.question"),
        translatable("faq.command.questions.problem-nrc-voice-chat.answer")
    ),
    CONNECTION_ISSUES(
        "problem-connection",
        translatable("faq.command.questions.problem-connection.question"),
        translatable("faq.command.questions.problem-connection.answer")
    ),
    READ_THE_DOCS(
        "read-the-docs",
        translatable("faq.command.questions.read-the-docs.question"),
        translatable("faq.command.questions.read-the-docs.answer")
    ),
    MAINTENANCE(
        "maintenance",
        translatable("faq.command.questions.maintenance.question"),
        translatable("faq.command.questions.maintenance.answer")
    ),
    HOW_TO_SHARE_LOG(
        "how-to-share-log",
        translatable("faq.command.questions.how-to-share-log.question"),
        translatable("faq.command.questions.how-to-share-log.answer")
    ),
    CLAN_INFO(
        "clan-info",
        translatable("faq.command.questions.clan-info.question"),
        translatable("faq.command.questions.clan-info.answer")
    ),
    TAKE_PART_IN_EVENT(
        "take-part-in-event",
        translatable("faq.command.questions.take-part-in-event.question"),
        translatable("faq.command.questions.take-part-in-event.answer")
    ),
    HOW_TO_JOIN(
        "how-to-join",
        translatable("faq.command.questions.how-to-join.question"),
        translatable("faq.command.questions.how-to-join.answer")
    ),
    ASK(
        "ask",
        translatable("faq.command.questions.ask.question"),
        translatable("faq.command.questions.ask.answer")
    ),
    MISSING_INFORMATION(
        "missing-information",
        translatable("faq.command.questions.missing-information.question"),
        translatable("faq.command.questions.missing-information.answer")
    ),
    HOW_TO_WHITELIST(
        "how-to-whitelist",
        translatable("faq.command.questions.how-to-whitelist.question"),
        translatable("faq.command.questions.how-to-whitelist.answer"),
        "gifs/wl-gif.gif"
    ),
    PING_PONG(
        "ping-pong",
        translatable("faq.command.questions.ping-pong.question"),
        translatable("faq.command.questions.ping-pong.answer")
    ),
}