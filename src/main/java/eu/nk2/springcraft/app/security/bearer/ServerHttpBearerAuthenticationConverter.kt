package eu.nk2.springcraft.app.security.bearer

import eu.nk2.springcraft.app.security.JwtTokenProvider
import eu.nk2.springcraft.app.security.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

import java.util.function.Function

class ServerHttpBearerAuthenticationConverter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService
): Function<ServerWebExchange, Mono<Authentication>> {

    override fun apply(serverWebExchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(serverWebExchange)
            .flatMap(AuthorizationHeaderPayload::extract)
            .filter(matchBearerLength)
            .flatMap(isolateBearerValue)
            .filter { jwtTokenProvider.validateToken(it) }
            .map { jwtTokenProvider.getUserIdFromJWT(it) }
            .flatMap { userDetailsService.findById(it) }
            .map { UsernamePasswordAuthenticationToken(it, null, it.authorities) }
            .doOnNext {
                SecurityContextHolder.getContext().authentication = it
            }.cast(Authentication::class.java)
    }

    companion object {
        private val BEARER = "Bearer "
        private val matchBearerLength = { authValue: String -> authValue.length > BEARER.length }
        private val isolateBearerValue = { authValue: String -> Mono.justOrEmpty(authValue.substring(BEARER.length)) }
    }
}
