package eu.nk2.springcraft.utils

import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.MediaType
import org.springframework.http.codec.HttpMessageEncoder
import org.springframework.util.MimeType
import reactor.core.publisher.Flux

class CombinedHttpMessageEncoder(
    private val encoders: List<HttpMessageEncoder<Any>>
): HttpMessageEncoder<Any> {
    override fun getEncodableMimeTypes(): MutableList<MimeType> =
        encoders.flatMap { it.encodableMimeTypes }
            .toMutableList()

    override fun getStreamingMediaTypes(): MutableList<MediaType> =
        encoders.flatMap { it.streamingMediaTypes }
            .toMutableList()

    override fun canEncode(elementType: ResolvableType, mimeType: MimeType?): Boolean =
        encoders.any { it.canEncode(elementType, mimeType) }

    override fun encodeValue(value: Any, bufferFactory: DataBufferFactory, valueType: ResolvableType, mimeType: MimeType?, hints: MutableMap<String, Any>?): DataBuffer {
        return encoders.find { mimeType in it.encodableMimeTypes }
            ?.encodeValue(value, bufferFactory, valueType, mimeType, hints)!!
    }

    override fun encode(inputStream: Publisher<out Any>, bufferFactory: DataBufferFactory, elementType: ResolvableType, mimeType: MimeType?, hints: MutableMap<String, Any>?): Flux<DataBuffer> {
        return encoders.find { mimeType in it.encodableMimeTypes }
            ?.encode(inputStream, bufferFactory, elementType, mimeType, hints)!!
    }

}
