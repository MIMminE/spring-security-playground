package nuts.learning.security_basic.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun userDetailService(): UserDetailsService {
        val admin = User.builder()
            .username("admin")
            .password("{noop}password123")  // {noop}은 암호화 없음
            .roles("ADMIN", "USER")
            .build()

        val user = User.builder()
            .username("user")
            .password("{noop}user123")
            .roles("USER")
            .build()

        val guest = User.builder()
            .username("guest")
            .password("{noop}guest123")
            .roles("GUEST")
            .build()

        return InMemoryUserDetailsManager(admin, user, guest)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/public").permitAll()  // 공개 접근 허용
                    .requestMatchers("/api/user").hasRole("USER")  // 인증 필요
                    .requestMatchers("/api/admin").hasRole("ADMIN")  // ADMIN 역할 필요
                    .anyRequest().authenticated()  // 나머지는 인증 필요
            }

            .httpBasic { }
            .anonymous { }
            .csrf { csrf -> csrf.disable() }
        return http.build()
    }
}