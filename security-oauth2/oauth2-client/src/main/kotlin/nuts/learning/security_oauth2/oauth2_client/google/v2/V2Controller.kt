package nuts.learning.security_oauth2.oauth2_client.google.v2

import org.springframework.context.annotation.Profile
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("google-v2")
class V2Controller {

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