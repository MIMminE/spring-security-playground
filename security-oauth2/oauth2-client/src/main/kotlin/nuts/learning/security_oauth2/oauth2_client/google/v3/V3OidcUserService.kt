package nuts.learning.security_oauth2.oauth2_client.google.v3

import nuts.learning.security_oauth2.oauth2_client.google.v3.repository.User
import nuts.learning.security_oauth2.oauth2_client.google.v3.repository.UserRepository
import org.springframework.context.annotation.Profile
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("v3")
@Component
class V3OidcUserService(private val userRepository: UserRepository) : OidcUserService() {


    /**
     * oidc 요청이 들어오면 User Repository 에 저장된 유저인지 아닌지를 파악하고 최초 로그인 유저에 대한 유저 등록 절차를 진행한다.
     * username : sub + "@" + issuer
     */

    @Transactional
    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        userRequest?.let {
            val extractedUserName = extractUsername(it)
            if (userRepository.existsByName(extractedUserName).not()) {
                // 신규 유저 등록 로직

                val user = User(
                    provider = it.clientRegistration.registrationId,
                    providerId = it.idToken.subject,
                    name = extractedUserName,
                    email = it.idToken.email,
                    picture = it.idToken.picture,
                    roles = listOf("ROLE_NEW_USER")
                )

                val newUser = userRepository.save(user)
                return toDefaultOidcUser(newUser, it.idToken)

            } else {
                val findUser = userRepository.findByName(extractedUserName)
                findUser?.let { user -> return toDefaultOidcUser(user, it.idToken) }
            }
        }

        return super.loadUser(userRequest)
    }

    fun extractUsername(userRequest: OidcUserRequest): String {
        val idToken = userRequest.idToken
        val sub = idToken.claims["sub"]?.toString()
            ?: throw IllegalArgumentException("ID Token does not contain 'sub' claim")
        val issuer = idToken.issuer?.toString()
            ?: throw IllegalArgumentException("ID Token does not contain 'iss' claim")

        return "$sub@$issuer"
    }

    fun toDefaultOidcUser(user: User, idToken: OidcIdToken): DefaultOidcUser {
        val authorities = user.roles.map { SimpleGrantedAuthority(it) }
        return DefaultOidcUser(authorities, idToken)
    }
}