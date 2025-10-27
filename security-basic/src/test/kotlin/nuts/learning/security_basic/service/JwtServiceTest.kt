package nuts.learning.security_basic.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = [
    "jwt.secret=mySecretKey123456789012345678901234567890",
    "jwt.expiration=3600000"
])
class JwtServiceTest {

    @Autowired
    private lateinit var jwtService: JwtService

    @Test
    fun `JWT 토큰 생성 테스트`() {
        // Given
        val username = "testuser"

        // When
        val token = jwtService.generateToken(username)

        // Then
        assertThat(token).isNotNull()
        assertThat(token).isNotEmpty()
        assertThat(token.split(".")).hasSize(3)
    }

    @Test
    fun `JWT 토큰에서 사용자명 추출 테스트`() {
        // Given
        val username = "testuser"
        val token = jwtService.generateToken(username)

        // When
        val extractedUsername = jwtService.extractUsername(token)

        // Then
        assertThat(extractedUsername).isEqualTo(username)
    }

    @Test
    fun `유효한 JWT 토큰 검증 테스트`() {
        // Given
        val username = "testuser"
        val token = jwtService.generateToken(username)

        // When
        val isValid = jwtService.validateToken(token)

        // Then
        assertThat(isValid).isTrue()
    }

    @Test
    fun `잘못된 JWT 토큰 검증 테스트`() {
        // Given
        val invalidToken = "invalid.token.here"

        // When
        val isValid = jwtService.validateToken(invalidToken)

        // Then
        assertThat(isValid).isFalse()
    }

    @Test
    fun `토큰 만료 확인 테스트`() {
        // Given
        val username = "testuser"
        val token = jwtService.generateToken(username)

        // When
        val isExpired = jwtService.isTokenExpired(token)

        // Then
        assertThat(isExpired).isFalse() // 새로 생성된 토큰은 만료되지 않음
    }
}