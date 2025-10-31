package nuts.learning.security_basic.cases.rest.jwt.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("rest-jwt")
class JwtAuthControllerTest @Autowired constructor(
    private val restTemplate: TestRestTemplate
) {

    @Test
    fun `JWT 토큰 생성 (로그인) 성공`() {
        val request = mapOf("username" to "apiuser", "password" to "apipass")
        val response = restTemplate.postForEntity(
            "/api/auth/login",
            request,
            String::class.java
        )

        assertEquals(200, response.statusCode.value())
        assert(response.body.toString().contains("token"))
    }


    @Test
    fun `로그인한 사용자 정보 조회 - me 성공`() {
        val loginReq = mapOf("username" to "apiuser", "password" to "apipass")
        val loginResp = restTemplate.postForEntity("/api/auth/login", loginReq, Map::class.java)
        assertEquals(200, loginResp.statusCode.value())

        val token = (loginResp.body?.get("token") as? String)
            ?: fail("토큰을 응답에서 찾을 수 없습니다")

        val headers = HttpHeaders().apply { setBearerAuth(token) }
        val entity = HttpEntity<Void>(headers)
        val meResp = restTemplate.exchange("/api/auth/me", HttpMethod.GET, entity, Map::class.java)
        assertEquals(200, meResp.statusCode.value())

        val body = meResp.body as Map<*, *>
        assertEquals("apiuser", body["principal"])
        val authorities = body["authorities"] as? List<*>
        assertNotNull(authorities)
        assertTrue { authorities.contains("ROLE_USER") }
    }
}
