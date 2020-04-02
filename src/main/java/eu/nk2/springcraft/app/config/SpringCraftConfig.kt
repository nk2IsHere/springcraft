package eu.nk2.springcraft.app.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.Resource

@Configuration
@PropertySource("classpath:springcraft.properties")
class SpringCraftConfig {
    @Value("file:private.key.der") var jwtPrivateKeyResource: Resource? = null
    @Value("file:public.key.der") var jwtPublicKeyResource: Resource? = null
    @Value("#{environment['springcraft.jwtExpiration']}") var jwtExpiration: Long? = null
    @Value("#{environment['springcraft.allowedWebHosts']}") var allowedWebHosts: Array<String>? = null
    @Value("#{environment['springcraft.securityAuthorizedPaths']}") var securityAuthorizedPaths: Array<String>? = null
    @Value("#{environment['springcraft.securityUnauthorizedPaths']}") var securityUnauthorizedPaths: Array<String>? = null
}
