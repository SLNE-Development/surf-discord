package dev.slne.discord.cooldown

enum class CooldownDuration {

    DONT_ASK_TO_ASK(60000),
    HOW_TO_JOIN(600000),
    FAQ(600000);

    val cooldown: Long

    constructor(cooldown: Long) {
        this.cooldown = cooldown
    }

}