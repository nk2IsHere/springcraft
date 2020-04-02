package eu.nk2.springcraft.app.config

import com.google.gson.Gson
import eu.nk2.springcraft.SpringCraft
import eu.nk2.springcraft.SpringCraft.Companion.logger
import eu.nk2.springcraft.utils.CombinedHttpMessageEncoder
import eu.nk2.springcraft.utils.Gson2JsonDecoder
import eu.nk2.springcraft.utils.Gson2JsonEncoder
import eu.nk2.springcraft.utils.String2HtmlEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.server.WebExceptionHandler

@Configuration
class LotteryWebConfig : WebFluxConfigurer {
    @Autowired lateinit var gson: Gson
    @Autowired private lateinit var config: SpringCraftConfig

    override fun addCorsMappings(registry: CorsRegistry) {
        logger.info("CORS allowed: ${(config.allowedWebHosts ?: arrayOf()).joinToString()}")
        registry.addMapping("/**")
            .allowedOrigins(*(config.allowedWebHosts ?: arrayOf()))
            .allowCredentials(true)
            .exposedHeaders("Authorization")
    }

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.customCodecs().register(CombinedHttpMessageEncoder(listOf(String2HtmlEncoder(), Gson2JsonEncoder(gson))))
        configurer.customCodecs().register(Gson2JsonDecoder(gson))

        configurer.registerDefaults(true)
    }

}
