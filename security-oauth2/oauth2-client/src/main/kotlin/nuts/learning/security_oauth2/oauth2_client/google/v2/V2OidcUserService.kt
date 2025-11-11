package nuts.learning.security_oauth2.oauth2_client.google.v2

import org.springframework.context.annotation.Profile
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Component

@Profile("google-v2")
@Component
class V2OidcUserService : OidcUserService() {

    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        userRequest?.let { req ->
            return toUserIdentity(req)
        } ?: throw OAuth2AuthenticationException(
            OAuth2Error("invalid_request", "Cannot extract user identity from request", null)
        )
    }

    fun toUserIdentity(userRequest: OidcUserRequest): OidcUser {

        val scopes = userRequest.accessToken.scopes.toMutableSet()

        val authorities = scopes.mapTo(mutableSetOf()) { scope ->
            when {
                scope.contains("email", ignoreCase = true) ->
                    SimpleGrantedAuthority("ROLE_EMAIL")

                scope.contains("profile", ignoreCase = true) ->
                    SimpleGrantedAuthority("ROLE_PROFILE")

                else ->
                    SimpleGrantedAuthority("ROLE_${scope.uppercase()}")
            }
        }

        if (authorities.isEmpty()) {
            authorities.add(SimpleGrantedAuthority("ROLE_GUEST"))
        }

        val idToken = userRequest.idToken

        return DefaultOidcUser(authorities, idToken)
    }
}