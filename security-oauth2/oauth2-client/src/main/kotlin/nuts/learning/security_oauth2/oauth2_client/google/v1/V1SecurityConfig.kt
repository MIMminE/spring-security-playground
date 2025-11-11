package nuts.learning.security_oauth2.oauth2_client.google.v1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@Profile("google-v1")
class V1SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/secure/**").hasAuthority("SCOPE_https://www.googleapis.com/auth/userinfo.email")
                    .anyRequest().authenticated()
            }
            .oauth2Login {
                it
                    .defaultSuccessUrl("/", true)
            } // 로그인 이후 항상 루트 경로로 리다이렉트

            .oauth2Client(Customizer.withDefaults())
            .csrf { it.disable() }

        return http.build()
    }
}