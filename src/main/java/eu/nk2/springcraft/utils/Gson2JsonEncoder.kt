package eu.nk2.springcraft.utils


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.MediaType
import org.springframework.http.codec.HttpMessageEncoder
import org.springframework.util.MimeType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.lang.NullPointerException
import java.nio.charset.StandardCharsets
import java.util.*

class Gson2JsonEncoder(
    private val gson: Gson
): HttpMessageEncoder<Any> {
    override fun getEncodableMimeTypes(): MutableList<MimeType> =
        mutableListOf(
            MimeType("application", "json", StandardCharsets.UTF_8),
            MimeType("application", "*+json", StandardCharsets.UTF_8)
        )

    override fun getStreamingMediaTypes(): MutableList<MediaType> =
        mutableListOf(MediaType.APPLICATION_STREAM_JSON)

    override fun canEncode(elementType: ResolvableType, mimeType: MimeType?): Boolean =
        true

    override fun encodeValue(value: Any, bufferFactory: DataBufferFactory, valueType: ResolvableType, mimeType: MimeType?, hints: MutableMap<String, Any>?): DataBuffer {
        val encodedValue = gson.toJson(value, valueType.type)
        val buffer = bufferFactory.allocateBuffer()
        val stream =  buffer.asOutputStream().bufferedWriter()
        stream.append(encodedValue)
        stream.flush()

        return buffer
    }

    override fun encode(inputStream: Publisher<out Any>, bufferFactory: DataBufferFactory, elementType: ResolvableType, mimeType: MimeType?, hints: MutableMap<String, Any>?): Flux<DataBuffer> {
        return (if(inputStream is Mono) Mono.from(inputStream).flux() else Flux.from(inputStream).collectList().flux())
            .map { gson.toJson(it, elementType.type) }
            .map {
                val buffer = bufferFactory.allocateBuffer()
                val stream =  buffer.asOutputStream().bufferedWriter()
                stream.append(it)
                stream.flush()

                buffer
            }
    }

}
