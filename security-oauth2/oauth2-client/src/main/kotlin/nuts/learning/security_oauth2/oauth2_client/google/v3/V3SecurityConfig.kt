package nuts.learning.security_oauth2.oauth2_client.google.v3

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Profile("v3")
@EnableWebSecurity
class V3SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        v3OidcUserService: V3OidcUserService
    ): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/", "/oauth2/**", "/login/**", "/h2-console/**").permitAll()
                    .requestMatchers("/secure/**").hasRole("NEW_USER")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .oauth2Login{ auth ->
                auth
                    .userInfoEndpoint { userInfo ->
                        userInfo.oidcUserService(v3OidcUserService)
                    }
                    .defaultSuccessUrl("/", true)
            }
            .oauth2Client(Customizer.withDefaults())
            .csrf { it.disable() }

        return http.build()
    }
}