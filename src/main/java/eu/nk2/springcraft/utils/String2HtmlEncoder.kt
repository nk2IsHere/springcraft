package eu.nk2.springcraft.utils


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
import java.nio.charset.StandardCharsets

class String2HtmlEncoder: HttpMessageEncoder<Any> {
    override fun getEncodableMimeTypes(): MutableList<MimeType> =
        mutableListOf(
            MimeType("text", "html", StandardCharsets.UTF_8),
            MimeType("text", "*+html", StandardCharsets.UTF_8),
            MimeType("text", "plain", StandardCharsets.UTF_8),
            MimeType("text", "*+plain", StandardCharsets.UTF_8)
        )

    override fun getStreamingMediaTypes(): MutableList<MediaType> =
        mutableListOf(
            MediaType.TEXT_EVENT_STREAM
        )

    override fun canEncode(elementType: ResolvableType, mimeType: MimeType?): Boolean =
        true

    override fun encodeValue(value: Any, bufferFactory: DataBufferFactory, valueType: ResolvableType, mimeType: MimeType?, hints: MutableMap<String, Any>?): DataBuffer {
        val buffer = bufferFactory.allocateBuffer()
        val stream =  buffer.asOutputStream().bufferedWriter()
        stream.append(value.toString())
        stream.flush()

        return buffer
    }

    override fun encode(inputStream: Publisher<out Any>, bufferFactory: DataBufferFactory, elementType: ResolvableType, mimeType: MimeType?, hints: MutableMap<String, Any>?): Flux<DataBuffer> {
        return (if(inputStream is Mono) Mono.from(inputStream).flux() else Flux.from(inputStream).collectList().flux())
            .map {
                it.toString()
            }
            .map {
                val buffer = bufferFactory.allocateBuffer()
                val stream =  buffer.asOutputStream().bufferedWriter()
                stream.append(it)
                stream.flush()

                buffer
            }
    }

}
