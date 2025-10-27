package nuts.learning.security_basic.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


//@RestController
//@RequestMapping("/api/auth")
//class AuthController(
//
//
//) {
//
//
//
//    @PostMapping("/login")
//    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> {
//        // 사용자 인증 후 JWT 토큰 생성
//        val token = jwtService.generateToken(loginRequest.username)
//        return ResponseEntity.ok(AuthResponse(token))
//    }
//}