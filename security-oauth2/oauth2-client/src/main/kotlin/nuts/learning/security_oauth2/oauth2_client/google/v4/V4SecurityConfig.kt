package nuts.learning.security_oauth2.oauth2_client.google.v4

import nuts.learning.security_oauth2.oauth2_client.google.v3.V3OidcUserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
@Profile("v4")
class V4SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, v3OidcUserService: V3OidcUserService): SecurityFilterChain {

        http
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                    .requestMatchers("/secure/**").hasRole("USER")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .oauth2Login { auth ->
                auth
                    .defaultSuccessUrl("/", true)
//                    .userInfoEndpoint { userInfo ->
//                        userInfo.oidcUserService(v3OidcUserService)
//                    }
            }
            .sessionManagement { sm ->

                sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 로그인 등 세션이 필요할때만 세션을 생성하며, 기본값이다.
                sm.sessionFixation().migrateSession() // 로그인 시 기존 세션을 재사용하지 않는 방식으로, 기본값이다.
                sm.maximumSessions(1).maxSessionsPreventsLogin(true)
                // 세션 초과 시 새로운 로그인 자체를 거부하는 옵션
            }
            .logout { logout ->
                logout
                    .invalidateHttpSession(true)
                    .deleteCookies("SESSION")
                    .logoutSuccessUrl("/")
            }


        return http.build()
    }

}