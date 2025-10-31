package nuts.learning.security_basic.cases.rest.jwt

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider
    private val expirationMillis = 1000 * 60 * 60 // 1시간

    @BeforeEach
    fun setUp() {
        jwtTokenProvider = JwtTokenProvider(expirationMillis.toLong())
    }

    @Test
    fun `토큰 생성 및 username 추출`() {
        val username = "testuser"
        val roles = listOf("USER", "ADMIN")
        val token = jwtTokenProvider.createToken(username, roles)

        val extractedUsername = jwtTokenProvider.getUsername(token)
        assertEquals(username, extractedUsername)
    }

    @Test
    fun `유효한 토큰 검증`() {
        val token = jwtTokenProvider.createToken("user", listOf("USER"))
        assertTrue(jwtTokenProvider.validateToken(token))
    }

    @Test
    fun `만료된 토큰 검증`() {
        val shortProvider = JwtTokenProvider(1) // 1ms 만료
        val token = shortProvider.createToken("user", listOf("USER"))
        Thread.sleep(5)
        assertFalse(shortProvider.validateToken(token))
    }

    @Test
    fun `잘못된 토큰 검증`() {
        val invalidToken = "invalid.token.value"
        assertFalse(jwtTokenProvider.validateToken(invalidToken))
    }
}