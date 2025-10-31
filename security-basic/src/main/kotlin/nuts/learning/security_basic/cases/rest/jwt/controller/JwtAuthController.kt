package nuts.learning.security_basic.cases.rest.jwt.controller

import nuts.learning.security_basic.cases.rest.jwt.JwtTokenProvider
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

/**
 * JWT 인증 컨트롤러 예제
 * - /login: JWT 토큰 발급
 * - /me: 인증된 사용자 정보 확인
 * - 학습 포인트: 인증 흐름, 토큰 전달/검증 방식
 */
@RestController
@RequestMapping("/api/auth")
@Profile("rest-jwt")
class JwtAuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) {

    data class LoginRequest(val username: String, val password: String)
    data class TokenResponse(val token: String)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val authorities = authentication.authorities.map { it.authority }
        val token = jwtTokenProvider.createToken(request.username, authorities)
        return ResponseEntity.ok(TokenResponse(token))
    }

    @GetMapping("/me")
    fun me(): ResponseEntity<Map<String, Any?>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication?.principal
        val authorities = authentication?.authorities?.map { it.authority } ?: emptyList()
        val info = mapOf(
            "principal" to principal?.toString(),
            "authorities" to authorities
        )
        return ResponseEntity.ok(info)
    }
}