package nuts.playgroud.spring_security.http_security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class AuthenticationManager {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // AuthenticationManagerBuilder는 인증 관련 설정을 정의하는 데 사용되는 빌더 객체
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        // 매니저 내부에서 실제 인증 로직을 담당하는 커스텀 프로바이더 클래스를 매니저 빌더에 등록한다.
        builder.authenticationProvider(new CustomAuthenticationProvider());

        org.springframework.security.authentication.AuthenticationManager manager = builder.build();

        http.authenticationManager(manager);

        return http.build();
    }


    // 커스텀 인증 프로바이더 구현
    private static class CustomAuthenticationProvider implements AuthenticationProvider {

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();

            // 간단한 인증 로직 (예: 사용자 이름과 비밀번호 확인)
            if ("admin".equals(username) && "password".equals(password)) {
                return new UsernamePasswordAuthenticationToken(username, password, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            }

            throw new BadCredentialsException("Invalid username or password");
        }

        @Override
        public boolean supports(Class<?> authentication) {
            // 인증 객체의 클래스 메타 정보를 가지고 실제 어떤 구현체의 토큰인지를 확인하는 방식으로
            // 주로 사용되는 패턴이다.
            return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
        }
    }
}