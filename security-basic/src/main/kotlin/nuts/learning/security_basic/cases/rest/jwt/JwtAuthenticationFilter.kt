package nuts.learning.security_basic.cases.rest.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JWT 토큰을 이용한 인증 필터 예제
 * - Authorization 헤더에서 Bearer 토큰 추출
 * - 토큰 유효성 검사 및 사용자 정보 파싱
 * - SecurityContext에 인증 정보 저장
 * - 학습 포인트: JWT 기반 인증의 동작 원리, 권한 처리 방식
 */
@Component
@Profile("rest-jwt")
class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)

            if (jwtTokenProvider.validateToken(token)) {
                val username = jwtTokenProvider.getUsername(token)
                val claims = jwtTokenProvider.getClaims(token)
                val roles = claims["roles"] as? Collection<*> ?: emptyList<Any>()
                val authorities =
                    roles.map { org.springframework.security.core.authority.SimpleGrantedAuthority(it.toString()) }
                val auth = UsernamePasswordAuthenticationToken(username, null, authorities)
                auth.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request, response)
    }
}