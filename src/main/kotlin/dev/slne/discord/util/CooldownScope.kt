package dev.slne.discord.util

enum class CooldownScope {

    DONT_ASK_TO_ASK(60000),
    HOW_TO_JOIN(600000),
    FAQ(600000);

    val cooldown: Long

    constructor(cooldown: Long) {
        this.cooldown = cooldown
    }

}