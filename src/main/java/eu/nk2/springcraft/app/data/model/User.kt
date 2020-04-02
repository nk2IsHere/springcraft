package eu.nk2.springcraft.app.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document data class User(
    @Id var id: String = UUID.randomUUID().toString(),
    var username: String,
    var password: String,
    var type: Type = Type.USER,
    var state: State = State.ACTIVE
) {
    enum class Type { ADMIN, USER }
    enum class State { ACTIVE, BANNED }

    override fun hashCode(): Int = javaClass.simpleName.hashCode() + id.hashCode()
    override fun toString(): String = "${javaClass.simpleName}:$id"
}
