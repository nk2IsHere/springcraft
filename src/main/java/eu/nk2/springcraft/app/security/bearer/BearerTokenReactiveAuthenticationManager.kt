package eu.nk2.springcraft.app.security.bearer

import eu.nk2.springcraft.app.config.ResourceDataMismatchException
import eu.nk2.springcraft.app.config.ResourceNotFoundException
import eu.nk2.springcraft.app.data.service.UserService
import eu.nk2.springcraft.app.security.UserPrincipal
import eu.nk2.springcraft.utils.errorIfEmpty
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class BearerTokenReactiveAuthenticationManager(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) : ReactiveAuthenticationManager {
    private val logger = LoggerFactory.getLogger(BearerTokenReactiveAuthenticationManager::class.java)

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val username = when(val principal = authentication.principal) {
            is UserPrincipal -> principal.username
            else -> principal.toString()
        }

        return userService.findByUsername(username.toMono())
            .errorIfEmpty(ResourceNotFoundException("User", "username", username))
            .filter {
                authentication.credentials == null
                    || authentication.credentials == it.password
                    || passwordEncoder.matches(authentication.credentials.toString(), it.password)
            }
            .errorIfEmpty(ResourceDataMismatchException("User[${authentication.principal!!}]", "password", "NOT DISCLOSED", authentication.credentials.toString()))
            .map {
                UsernamePasswordAuthenticationToken(
                    UserPrincipal.create(it),
                    it.password,
                    arrayListOf(
                        SimpleGrantedAuthority("ROLE_${it.type}"),
                        SimpleGrantedAuthority("STATE_${it.state}")
                    )
                )
            }
    }
}
