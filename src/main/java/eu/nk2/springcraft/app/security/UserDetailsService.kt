package eu.nk2.springcraft.app.security

import eu.nk2.springcraft.app.config.ResourceNotFoundException
import eu.nk2.springcraft.app.data.service.UserService
import eu.nk2.springcraft.utils.errorIfEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class UserDetailsService: ReactiveUserDetailsService {
    @Autowired private lateinit var userService: UserService

    override fun findByUsername(username: String?): Mono<UserDetails> =
        userService.findByUsername(Mono.justOrEmpty(username))
            .errorIfEmpty(UsernameNotFoundException("User with login $username not found"))
            .map { UserPrincipal.create(it) }

    fun findById(id: String?): Mono<UserDetails> =
        userService.findById(Mono.justOrEmpty(id))
            .errorIfEmpty(ResourceNotFoundException("User", "id", id))
            .map { UserPrincipal.create(it) }
}
