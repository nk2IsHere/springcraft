package eu.nk2.springcraft.app.security

import eu.nk2.springcraft.app.data.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserPrincipal(
    val id: String?,
    private val username: String,
    @Transient private val password: String,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {

    override fun getUsername(): String =
        username

    override fun getPassword(): String =
        password

    override fun getAuthorities(): Collection<GrantedAuthority> =
        authorities

    override fun isAccountNonExpired(): Boolean =
        true

    override fun isAccountNonLocked(): Boolean =
        this.authorities.contains(SimpleGrantedAuthority("STATE_ACTIVE"))

    override fun isCredentialsNonExpired(): Boolean =
        true

    override fun isEnabled(): Boolean =
        this.authorities.contains(SimpleGrantedAuthority("STATE_ACTIVE"))

    companion object {
        fun create(user: User): UserPrincipal {
            val authorities = arrayListOf(
                SimpleGrantedAuthority("ROLE_${user.type}"),
                SimpleGrantedAuthority("STATE_${user.state}")
            )

            return UserPrincipal(
                user.id,
                user.username,
                user.password,
                authorities
            )
        }
    }
}
