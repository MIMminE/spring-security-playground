package nuts.learning.security_oauth2.oauth2_client.google.v1

import org.springframework.context.annotation.Profile
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("google-v1")
class V1Controller {

    /**
     * @AuthenticationPrincipal -> 인증된 주체(principal) 정보를 가져올 때 사용
     * OAuth2User -> OAuth2 인증을 통해 얻어진 사용자 정보를 나타내는 인터페이스
     * 코틀린 nullable 안전 호출 연산자(?.)를 사용하여 principal이 null일 경우에도 안전하게 접근
     */
    @GetMapping("/pass")
    fun pass(authentication: Authentication?): Map<String, Any?> {
        return mapOf(
            "auth_principal_type" to (authentication?.principal?.javaClass?.name),
            "auth_principal" to (authentication?.principal?.toString()),
            "authorities" to (authentication?.authorities?.map { it.authority }),
            "credentials" to (authentication?.credentials?.toString()),
            "details" to (authentication?.details?.toString())
        )
    }

    @GetMapping("/secure")
    fun secure(authentication: Authentication?): Map<String, Any?> {
        return mapOf(
            "auth_principal_type" to (authentication?.principal?.javaClass?.name),
            "auth_principal" to (authentication?.principal?.toString()),
            "authorities" to (authentication?.authorities?.map { it.authority }),
            "credentials" to (authentication?.credentials?.toString()),
            "details" to (authentication?.details?.toString()),
        )
    }
}