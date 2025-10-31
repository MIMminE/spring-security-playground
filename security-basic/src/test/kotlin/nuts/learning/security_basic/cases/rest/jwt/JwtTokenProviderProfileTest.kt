package nuts.learning.security_basic.cases.rest.jwt

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    properties = [
        "security.jwt.secret=mytestsecretkeymytestsecretkey123456",
        "security.jwt.expiration=3600000"
    ]
)
@ActiveProfiles("rest-jwt")
class JwtTokenProviderProfileTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun `rest-jwt 프로파일에서 JwtTokenProvider 빈이 등록된다`() {
        assertNotNull(context.getBean(JwtTokenProvider::class.java))
    }
}