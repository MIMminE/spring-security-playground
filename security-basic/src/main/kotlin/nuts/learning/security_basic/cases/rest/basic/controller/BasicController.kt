package nuts.learning.security_basic.cases.rest.basic.controller

import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("rest-basic")
@RequestMapping("/api")
class BasicController {

    @GetMapping("/public")
    fun publicEndpoint() = "누구나 접근 가능"

    @GetMapping("/user")
    fun userEndpoint() = "인증된 사용자"

    @GetMapping("/admin")
    fun adminEndpoint() = "관리자만"
}
