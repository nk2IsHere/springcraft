package eu.nk2.springcraft.app.security.bearer

import org.springframework.http.HttpHeaders
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

object AuthorizationHeaderPayload {
    fun extract(serverWebExchange: ServerWebExchange): Mono<String> {
        return Mono.justOrEmpty(serverWebExchange.request
            .headers
            .getFirst(HttpHeaders.AUTHORIZATION))
    }
}
