package nuts.learning.security_basic.cases.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession
import org.springframework.security.web.session.SessionInformationExpiredStrategy
import org.springframework.security.web.session.InvalidSessionStrategy
import org.springframework.security.web.session.SessionInformationExpiredEvent

/**
 * Redis를 세션 저장소로 사용하는 Security 설정
 * - Spring Session Redis를 사용하여 세션을 Redis에 저장
 * - 한 계정당 하나의 세션만 허용
 * 활성화: spring.profiles.active=web-redis
 */
@Configuration
@EnableWebSecurity
@EnableRedisHttpSession // Spring Session Redis를 활성화
@Profile("web-redis")
class WebRedisSecurityConfig {

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
    fun securityFilterChain(
        http: HttpSecurity,
        sessionRegistry: SessionRegistry,
        expiredStrategy: SessionInformationExpiredStrategy,
        invalidSessionStrategy: InvalidSessionStrategy
    ): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/", "/login", "/public", "/session-expired", "/session-invalid").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form.defaultSuccessUrl("/", true)
            }
            .logout { logout ->
                logout.invalidateHttpSession(true)
                    .deleteCookies("SESSION")
                    .logoutSuccessUrl("/")
            }
            .sessionManagement { sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // 세션이 유효하지 않을 때 처리하는 전략 (예: 타임아웃으로 인해 세션이 없는 경우)
                sm.invalidSessionStrategy(invalidSessionStrategy)
                sm.sessionFixation().migrateSession()
                sm.maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                    .sessionRegistry(sessionRegistry)
                    .expiredSessionStrategy(expiredStrategy)
            }
        return http.build()
    }

    @Bean
    fun sessionRegistry(): SessionRegistry {
        return SessionRegistryImpl()
    }

    @Bean
    fun httpSessionEventPublisher(): HttpSessionEventPublisher {
        return HttpSessionEventPublisher()
    }

    // 동시 로그인으로 인해 기존 세션이 만료되었을 때 호출되는 핸들러
    @Bean
    fun sessionInformationExpiredStrategy(): SessionInformationExpiredStrategy {
        return SessionInformationExpiredStrategy { event: SessionInformationExpiredEvent ->
            val request: HttpServletRequest = event.request
            val response: HttpServletResponse = event.response
            // val sessionInformation = event.sessionInformation // 필요 시 사용

            val accept = request.getHeader("Accept") ?: ""
            val xRequestedWith = request.getHeader("X-Requested-With") ?: ""
            if (xRequestedWith == "XMLHttpRequest" || accept.contains("application/json")) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = "application/json;charset=UTF-8"
                response.writer.write("{\"message\":\"세션 만료: 다른 곳에서 로그인되었습니다.\"}")
            } else {
                // 정적 리소스(session-expired.html)를 포워드하여 클라이언트에게 전달
                try {
                    request.getRequestDispatcher("/session-expired.html").forward(request, response)
                } catch (ex: Exception) {
                    // 포워드 실패 시 대체로 리다이렉트
                    response.sendRedirect("/session-expired")
                }
            }
        }
    }

    // 세션이 유효하지 않을 때(예: 타임아웃 후 요청) 호출되는 핸들러
    @Bean
    fun invalidSessionStrategy(): InvalidSessionStrategy {
        return InvalidSessionStrategy { request: HttpServletRequest, response: HttpServletResponse ->
            val accept = request.getHeader("Accept") ?: ""
            val xRequestedWith = request.getHeader("X-Requested-With") ?: ""
            if (xRequestedWith == "XMLHttpRequest" || accept.contains("application/json")) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = "application/json;charset=UTF-8"
                response.writer.write("{\"message\":\"세션이 유효하지 않습니다. 다시 로그인해주세요.\"}")
            } else {
                // 정적 리소스(session-invalid.html)를 포워드하여 클라이언트에게 전달
                try {
                    request.getRequestDispatcher("/session-invalid.html").forward(request, response)
                } catch (ex: Exception) {
                    response.sendRedirect("/session-invalid")
                }
            }
        }
    }
}
