package eu.nk2.springcraft.utils

import com.google.gson.Gson
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Hints
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.HttpMessageDecoder
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.util.MimeType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

class Gson2JsonDecoder(
    private val gson: Gson
): HttpMessageDecoder<Any> {
    private val maxInMemorySize = 1024 * 1024

    override fun getDecodableMimeTypes(): MutableList<MimeType> =
        mutableListOf(
            MimeType("application", "json", StandardCharsets.UTF_8),
            MimeType("application", "*+json", StandardCharsets.UTF_8)
        )
    override fun canDecode(elementType: ResolvableType, mimeType: MimeType?): Boolean =
        true

    override fun decode(inputStream: Publisher<DataBuffer>, elementType: ResolvableType, mimeType: MimeType?, hints: MutableMap<String, Any>?): Flux<Any> =
        Flux.from(DataBufferUtils.join(inputStream, this.maxInMemorySize))
            .map { gson.fromJson<Any>(
                it.asInputStream().bufferedReader().readText(),
                elementType.type
            ) }

    override fun decodeToMono(inputStream: Publisher<DataBuffer>, elementType: ResolvableType, mimeType: MimeType?, hints: MutableMap<String, Any>?): Mono<Any> =
        Mono.from(DataBufferUtils.join(inputStream, this.maxInMemorySize))
            .map { gson.fromJson<Any>(
                it.asInputStream().bufferedReader().readText(),
                elementType.type
            ) }

    override fun getDecodeHints(actualType: ResolvableType, elementType: ResolvableType, request: ServerHttpRequest, response: ServerHttpResponse): MutableMap<String, Any> =
        Hints.none()
}
