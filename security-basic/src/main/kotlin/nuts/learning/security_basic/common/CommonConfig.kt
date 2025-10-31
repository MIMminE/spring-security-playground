package nuts.learning.security_basic.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

class CommonConfig {
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
}