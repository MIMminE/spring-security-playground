package nuts.learning.security_basic.cases.rest.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile("rest-jwt")
class JwtTokenProvider(
    @Value("\${security.jwt.expiration:3600000}") private val expirationMillis: Long
) {

    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val jwtParser = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()

    fun createToken(username: String, roles: List<String>): String =
        Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationMillis))
            .signWith(secretKey)
            .compact()

    fun getUsername(token: String): String =
        jwtParser.parseClaimsJws(token).body.subject

    fun validateToken(token: String): Boolean =
        try {
            val claims = jwtParser.parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }

    fun getClaims(token: String): Map<String, Any> =
        jwtParser.parseClaimsJws(token).body.entries.associate { it.key to it.value }
}