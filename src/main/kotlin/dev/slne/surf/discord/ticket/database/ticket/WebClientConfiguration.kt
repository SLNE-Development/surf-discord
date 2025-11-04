package dev.slne.surf.discord.ticket.database.ticket

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.discord.getBean
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@Component
class WebClientConfiguration {

    @Bean
    fun caffeineConfig() = Caffeine.newBuilder().expireAfterWrite(60.minutes.toJavaDuration())

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>) = CaffeineCacheManager().apply {
        this.setCaffeine(caffeine)
    }

    @Bean
    fun webClient() = WebClient.builder().baseUrl("https://api.castcrafter.de/v1")
        .defaultHeaders { headers ->
            headers.set("Authorization", "Bearer abcd")
            headers.set("User-Agent", "SurfDiscordBot/1.0")
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)

            headers.setCacheControl(CacheControl.noCache())
        }
        .codecs { configurer ->
            configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
        }
        .build()

    @Bean
    fun webClientAdapter(webClient: WebClient) =
        WebClientAdapter.create(webClient)

    @Bean
    fun webClientFactory(adapter: WebClientAdapter) =
        HttpServiceProxyFactory.builderFor(adapter).build()

    @Bean
    fun webClientFactory() = getBean<HttpServiceProxyFactory>()

}

inline fun <reified T : Any> HttpServiceProxyFactory.createClient() =
    this.createClient(T::class.java)