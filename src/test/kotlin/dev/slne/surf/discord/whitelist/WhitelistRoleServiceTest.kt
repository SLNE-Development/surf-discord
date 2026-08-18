package dev.slne.surf.discord.whitelist

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertSame

class WhitelistRoleServiceTest {
    @Test
    fun `uses the fully loaded member cache for synchronization`() {
        val cachedMembers = emptyList<Member>()
        val guild = Proxy.newProxyInstance(
            Guild::class.java.classLoader,
            arrayOf(Guild::class.java)
        ) { _, method, _ ->
            when (method.name) {
                "isLoaded" -> true
                "getMembers" -> cachedMembers
                else -> error("Unexpected Guild call: ${method.name}")
            }
        } as Guild

        assertSame(cachedMembers, cachedMembersForWhitelistSync(guild))
    }
}
