package dev.slne.surf.discord.command.console

interface ConsoleCommand {
    val name: String
    fun execute(args: List<String>)
}
