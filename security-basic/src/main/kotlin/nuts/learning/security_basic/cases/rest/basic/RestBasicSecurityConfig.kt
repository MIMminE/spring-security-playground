package nuts.learning.security_basic.cases.rest.basic

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Profile("rest-basic")
class RestBasicSecurityConfig {

    @Bean
    fun userDetailsService(): UserDetailsService {
        val apiUser = User.builder()
            .username("apiuser")
            .password("{noop}apipass")
            .roles("USER")
            .build()

        val admin = User.builder()
            .username("admin")
            .password("{noop}adminpass")
            .roles("ADMIN", "USER")
            .build()

        return InMemoryUserDetailsManager(apiUser, admin)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // REST: 보통 비활성
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/public").permitAll()
                    .requestMatchers("/api/user").authenticated()
                    .requestMatchers("/api/admin").hasRole("ADMIN")
                    .anyRequest().denyAll()
            }
            .httpBasic { } // 간단한 예시: HTTP Basic 사용
        return http.build()
    }
}