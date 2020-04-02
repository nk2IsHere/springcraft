package eu.nk2.springcraft.app.security

import eu.nk2.springcraft.SpringCraft.Companion.logger
import eu.nk2.springcraft.app.config.ResourceNotFoundException
import eu.nk2.springcraft.app.config.SpringCraftConfig
import eu.nk2.springcraft.utils.getPrivateKey
import eu.nk2.springcraft.utils.getPublicKey
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtTokenProvider {
    @Autowired private lateinit var config: SpringCraftConfig

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal

        val now = Date()
        val expiryDate = Date(now.time + (config.jwtExpiration ?: 0L))

        return Jwts.builder()
            .setSubject(userPrincipal.id)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.RS256,
                config.jwtPrivateKeyResource
                    ?.file
                    ?.readBytes()
                    ?.getPrivateKey("RSA")
                    ?: throw ResourceNotFoundException("file", "private key", null)
            )
            .compact()
    }

    fun getUserIdFromJWT(token: String): String {
        val claims = Jwts.parser()
            .setSigningKey(
                config.jwtPublicKeyResource
                    ?.file
                    ?.readBytes()
                    ?.getPublicKey("RSA")
                    ?: throw ResourceNotFoundException("file", "public key", null)
            )
            .parseClaimsJws(token)
            .body

        return claims.subject.toString()
    }

    private val singingKeyResolver: SigningKeyResolver =  object: SigningKeyResolver {
        private fun checkSingingAlgorithm(algorithm: String) = algorithm == SignatureAlgorithm.RS256.value

        override fun resolveSigningKey(header: JwsHeader<out JwsHeader<*>>, claims: Claims): Key {
            if(!checkSingingAlgorithm(header["alg"].toString()))
                throw UnsupportedJwtException("Unsupported algorithm signature detected! Required RS256, found ${header["alg"]}.")
            return config.jwtPublicKeyResource
                ?.file
                ?.readBytes()
                ?.getPublicKey("RSA")
                ?: throw ResourceNotFoundException("file", "public key", null)
        }

        override fun resolveSigningKey(header: JwsHeader<out JwsHeader<*>>, plaintext: String): Key {
            if(!checkSingingAlgorithm(header["alg"].toString()))
                throw UnsupportedJwtException("Unsupported algorithm signature detected! Required RS256, found ${header["alg"]}.")
            return config.jwtPublicKeyResource
                ?.file
                ?.readBytes()
                ?.getPublicKey("RSA")
                ?: throw ResourceNotFoundException("file", "public key", null)
        }

    }

    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser()
                .setSigningKeyResolver(this.singingKeyResolver)
                .parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.error("Request to parse JWT with invalid signature : {} failed : {}", authToken, ex.message)
        } catch (ex: MalformedJwtException) {
            logger.error("Request to parse invalid JWT : {} failed : {}", authToken, ex.message)
        } catch (ex: ExpiredJwtException) {
            logger.error("Request to parse expired JWT : {} failed : {}", authToken, ex.message)
        } catch (ex: UnsupportedJwtException) {
            logger.error("Request to parse unsupported JWT : {} failed : {}", authToken, ex.message)
        } catch (ex: IllegalArgumentException) {
            logger.error("Request to parse empty or null JWT : {} failed : {}", authToken, ex.message)
        }

        return false
    }
}
