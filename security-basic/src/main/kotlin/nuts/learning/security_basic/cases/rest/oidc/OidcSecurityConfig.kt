package nuts.learning.security_basic.cases.rest.oidc

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

class OidcSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .oauth2Login {  }
            .oauth2ResourceServer {  }

        return http.build()
    }
}