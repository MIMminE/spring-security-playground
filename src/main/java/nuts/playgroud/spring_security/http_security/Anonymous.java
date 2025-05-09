package nuts.playgroud.spring_security.http_security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Configuration
@EnableWebSecurity
public class Anonymous {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(Anonymous.class)
    public @interface EnableAnonymousTemplate {
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.anonymous(anonymous -> anonymous
                .principal("guest") // 익명 사용자의 주체의 값을 guest로 설정한다. 기본값은 anonymousUser 이다.
                .authorities("ROLE_GUEST") // 익명 사용자에게 "ROLE_GUEST" 권한을 부여한다.
        );
        return http.build();
    }
}
