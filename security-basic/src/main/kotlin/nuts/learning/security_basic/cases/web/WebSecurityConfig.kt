package nuts.learning.security_basic.cases.web

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

/**
 * 상태 기반(세션) 애플리케이션용 Security 설정
 * - 폼 로그인 사용
 * - 한 계정당 하나의 세션만 허용
 * - 새 로그인 시 기존 세션을 만료시키도록 구성
 * 활성화: spring.profiles.active=web (또는 실행 시 --spring.profiles.active=web)
 */
@Configuration
@EnableWebSecurity
@Profile("web")
class WebSecurityConfig {

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
    fun securityFilterChain(http: HttpSecurity, sessionRegistry: SessionRegistry): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/", "/login", "/public").permitAll()
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
                // 세션 필요 시에만 생성. 기본값(IF_REQUIRED)
                sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                sm.sessionFixation().migrateSession()
                // 동시 세션 제어: 한 계정당 최대 1개 세션
                sm.maximumSessions(1)
                    .maxSessionsPreventsLogin(true) // 새 로그인 시 기존 세션을 만료시키려면 false
                    .sessionRegistry(sessionRegistry)
            }
        return http.build()
    }

    @Bean
    fun sessionRegistry(): SessionRegistry {
        return SessionRegistryImpl()
    }

    // HttpSessionEventPublisher를 등록해야 세션 만료 등의 이벤트가 Spring Security의 세션 레지스트리에 반영됩니다.
    @Bean
    fun httpSessionEventPublisher(): HttpSessionEventPublisher {
        return HttpSessionEventPublisher()
    }
}