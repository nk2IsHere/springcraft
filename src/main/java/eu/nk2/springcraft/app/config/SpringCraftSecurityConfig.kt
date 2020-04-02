package eu.nk2.springcraft.app.config

import eu.nk2.springcraft.SpringCraft.Companion.logger
import eu.nk2.springcraft.app.data.service.UserService
import eu.nk2.springcraft.app.security.JwtTokenProvider
import eu.nk2.springcraft.app.security.UserDetailsService
import eu.nk2.springcraft.app.security.bearer.BearerTokenReactiveAuthenticationManager
import eu.nk2.springcraft.app.security.bearer.ServerHttpBearerAuthenticationConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SpringCraftSecurityConfig {
    @Autowired private lateinit var config: SpringCraftConfig

    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var userDetailsService: UserDetailsService
    @Autowired private lateinit var jwtTokenProvider: JwtTokenProvider

    @Bean fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean fun reactiveAuthenticationManager() = BearerTokenReactiveAuthenticationManager(
        userService,
        passwordEncoder()
    )

    private fun bearerAuthenticationFilter(): AuthenticationWebFilter {
        val bearerAuthenticationFilter = AuthenticationWebFilter(reactiveAuthenticationManager())
        val bearerConverter = ServerHttpBearerAuthenticationConverter(
            jwtTokenProvider,
            userDetailsService
        )

        bearerAuthenticationFilter.setServerAuthenticationConverter {
            bearerConverter.apply(it)
        }
        return bearerAuthenticationFilter
    }


    @Bean fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        logger.info("Authorized paths: ${(config.securityAuthorizedPaths ?: arrayOf()).joinToString()}")
        logger.info("Unauthorized paths: ${(config.securityUnauthorizedPaths ?: arrayOf()).joinToString()}")
        http.csrf().disable()
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS)
            .permitAll()
            .and()
            .authorizeExchange()
            .pathMatchers(*(config.securityUnauthorizedPaths ?: arrayOf()))
            .permitAll()
            .and()
            .authorizeExchange()
            .pathMatchers(*(config.securityAuthorizedPaths ?: arrayOf()))
            .authenticated()
            .and()
            .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)

        return http.build()
    }
}
