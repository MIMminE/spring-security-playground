package nuts.learning.security_basic.cases.rest.jwt

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


/**
 * JWT 인증 방식 Security 설정 예제
 * - 세션 미사용(STATELESS)
 * - JWT 인증 필터 등록
 * - 엔드포인트별 권한 부여
 * - 학습 포인트: Spring Security의 필터 체인, 프로파일별 설정 분리
 */
@Configuration
@EnableWebSecurity
@Profile("rest-jwt")
class JwtSecurityConfig {

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

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
    fun filterChain(http: HttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/auth/login").permitAll()
                    .requestMatchers("/api/auth/*").hasRole("USER")
                    .anyRequest().denyAll()
            }
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}