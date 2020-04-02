package eu.nk2.springcraft.app.config

import com.github.debop.kodatimes.toDateTime
import com.github.salomonbrys.kotson.jsonSerializer
import com.github.salomonbrys.kotson.toJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import eu.nk2.springcraft.utils.NaturalDeserializer
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.*

@Configuration
@Import(GsonAutoConfiguration::class)
class LotteryGsonConfig {

    @Bean fun gson(): Gson = GsonBuilder()
        .registerTypeAdapter(Any::class.java, NaturalDeserializer())
        .registerTypeAdapter(Date::class.java, jsonSerializer<Date> { (src, _, _) -> src.toDateTime().toString("yyyy-MM-dd HH:mm:ss").toJson() })
        .serializeSpecialFloatingPointValues()
        .create()

}
