package nuts.learning.security_oauth2.oauth2_client.google.v2

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Profile("google-v2")
@EnableWebSecurity
class V2SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        oidcUserService: V2OidcUserService
    ): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                    .requestMatchers("/secure/**").hasRole("EMAIL")
                    .anyRequest().authenticated()
            }
            .oauth2Login { auth ->
                auth
                    .userInfoEndpoint { userInfo ->
                        userInfo.oidcUserService(oidcUserService)
                    }
                    .defaultSuccessUrl("/", true)
            }
            .oauth2Client(Customizer.withDefaults())
            .csrf { it.disable() }

        return http.build()
    }
}

