package nuts.learning.security_basic.controller

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class BasicController {

    // 공개 접근 가능한 엔드포인트
    @GetMapping("/public")
    fun publicEndpoint(authentication: Authentication?): String {

        println("authentication: $authentication")
        println("authentication?.name: ${authentication?.name}")
        println("authentication?.principal: ${authentication?.principal}")
        println("authentication?.authorities: ${authentication?.authorities}")
        println("authentication?.isAuthenticated: ${authentication?.isAuthenticated}")

        val contextAuth = SecurityContextHolder.getContext().authentication
        println("authentication: $contextAuth")
        println("authentication?.name: ${contextAuth?.name}")
        println("authentication?.principal: ${contextAuth?.principal}")
        println("authentication?.authorities: ${contextAuth?.authorities}")
        println("authentication?.isAuthenticated: ${contextAuth?.isAuthenticated}")

        return "이 엔드포인트는 누구나 접근 가능합니다"
    }

    // 인증된 사용자만 접근 가능
    @GetMapping("/user")
    fun userEndpoint(authentication: Authentication): String {
        println("authentication?.principal: ${authentication.principal}")
        return "인증된 사용자만 접근 가능합니다."
    }

    // 관리자 권한을 가진 사용자만 접근 가능
    @GetMapping("/admin")
    fun adminEndpoint(): String {
        return "관리자만 접근 가능합니다."
    }
}