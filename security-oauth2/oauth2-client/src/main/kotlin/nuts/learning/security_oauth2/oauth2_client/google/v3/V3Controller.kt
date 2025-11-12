package nuts.learning.security_oauth2.oauth2_client.google.v3

import nuts.learning.security_oauth2.oauth2_client.google.v3.repository.UserRepository
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("v3", "v4")
class V3Controller(private val userRepository: UserRepository) {

    // post 요청으로 기존에 있는 user 정보에 admin 권한 추가하는 메서드 생성
    // 일반적으로 Repository 를 Controller 에 직접 주입하지 않고 Service 레이어를 통해 처리하는 것이 좋지만,
    // 이 예제에서는 간단함을 위해 직접 주입하는 방식을 사용합니다

    @PostMapping("/make-admin")
    @Transactional
    fun makeAdmin(authentication: Authentication?): Map<String, Any?> {

        val oidcUser = (authentication?.principal as? OidcUser)
            ?: return mapOf("status" to "failed", "reason" to "principal is not OidcUser")

        val idToken = oidcUser.idToken
        val sub = idToken.claims["sub"]?.toString()
            ?: throw IllegalArgumentException("ID Token does not contain 'sub' claim")
        val issuer = idToken.issuer?.toString()
            ?: throw IllegalArgumentException("ID Token does not contain 'iss' claim")

        val userName = "$sub@$issuer"

        val user = userRepository.findByName(userName)
            ?: return mapOf("status" to "not_found", "userName" to userName)

        if (!user.roles.contains("ROLE_ADMIN")) {
            user.roles += "ROLE_ADMIN"
        }

        // 추가된 룰을 현재 인증 세션에 반영하기
        // 현재 SecurityContext의 Authentication을 가져와 새로운 권한 목록으로 대체
        val currentAuth = SecurityContextHolder.getContext().authentication
            ?: return mapOf("status" to "failed", "reason" to "no authentication in context")

        val newAuthorities = currentAuth.authorities.toMutableList()
        if (newAuthorities.none { it.authority == "ROLE_ADMIN" }) {
            newAuthorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
        }

        val newAuth = UsernamePasswordAuthenticationToken(
            currentAuth.principal,
            currentAuth.credentials,
            newAuthorities
        ).apply { details = currentAuth.details }

        SecurityContextHolder.getContext().authentication = newAuth

        return mapOf(
            "status" to "ok",
            "userName" to userName,
            "roles" to user.roles
        )
    }

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

    @GetMapping("/admin")
    fun admin(authentication: Authentication?): Map<String, Any?> {
        return mapOf(
            "auth_principal_type" to (authentication?.principal?.javaClass?.name),
            "auth_principal" to (authentication?.principal?.toString()),
            "authorities" to (authentication?.authorities?.map { it.authority }),
            "credentials" to (authentication?.credentials?.toString()),
            "details" to (authentication?.details?.toString()),
        )
    }
}