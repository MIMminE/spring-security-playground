package nuts.learning.security_basic.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtService {
//    // 32자 이상 HMAC SHA KEY 는 32자(256비트) 이상을 필요로 한다.
//    private val secretKey = "mySecretKey123456789012345678901234567890"
//    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${jwt.expiration}")
    private val expiration: Long = 3600000

    private val key: Key by lazy {
        if (secretKey.length < 32) {
            throw IllegalArgumentException("JWT secret must be at least 32 characters long")
        }
        Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    // jwtParser 정의 추가
    private val jwtParser by lazy {
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
    }

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setExpiration(Date(System.currentTimeMillis() + 3600000))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            jwtParser.parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    // 추가 유용한 메서드들
    fun extractUsername(token: String): String {
        return jwtParser.parseClaimsJws(token).body.subject
    }

    fun isTokenExpired(token: String): Boolean {
        val claims = jwtParser.parseClaimsJws(token).body
        return claims.expiration.before(Date())
    }
}