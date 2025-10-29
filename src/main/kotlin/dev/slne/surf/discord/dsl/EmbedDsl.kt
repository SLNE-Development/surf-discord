package dev.slne.surf.discord.dsl

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

@DslMarker
annotation class EmbedDsl

@EmbedDsl
class EmbedFieldDsl {
    var name: String = ""
    var value: String = ""
    var inline: Boolean = false
}

@EmbedDsl
class EmbedBuilderDsl {
    var title: String? = null
    var description: String? = null
    var color: Color? = null
    var footer: String? = null
    var image: String? = null
    var thumbnail: String? = null

    private val fields = mutableListOf<EmbedFieldDsl>()

    fun field(block: EmbedFieldDsl.() -> Unit) {
        val f = EmbedFieldDsl()
        f.block()
        fields.add(f)
    }

    fun build(): EmbedBuilder {
        val embed = EmbedBuilder()
        title?.let { embed.setTitle(it) }
        description?.let { embed.setDescription(it) }
        color?.let { embed.setColor(it) }
        footer?.let { embed.setFooter(it) }
        image?.let { embed.setImage(it) }
        thumbnail?.let { embed.setThumbnail(it) }
        fields.forEach { embed.addField(it.name, it.value, it.inline) }
        return embed
    }
}

fun embed(block: EmbedBuilderDsl.() -> Unit): MessageEmbed {
    val dsl = EmbedBuilderDsl()
    dsl.block()
    return dsl.build().build()
}
