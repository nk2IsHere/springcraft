package eu.nk2.springcraft.app.data.service

import eu.nk2.springcraft.app.data.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Repository interface UserRepository : ReactiveCrudRepository<User, String> {
    fun findFirstByUsername(username: Mono<String>): Mono<User>
}

@Service class UserService {
    @Autowired private lateinit var userRepository: UserRepository

    fun findById(id: Mono<String>) =
        userRepository.findById(id)

    fun findByUsername(username: Mono<String>) =
        userRepository.findFirstByUsername(username)

    fun save(user: User) =
        userRepository.save(user)
}
