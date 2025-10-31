package nuts.learning.security_basic.cases.rest.basic.config


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("rest-basic")
class BasicCaseIntegrationTest @Autowired constructor(
    private val restTemplate: TestRestTemplate
) {

    @Test
    fun `public endpoint accessible without auth`() {
        val resp = restTemplate.getForEntity("/api/public", String::class.java)
        assertEquals(HttpStatus.OK, resp.statusCode)
        assertTrue(resp.body?.contains("누구나") == true)
    }

    @Test
    fun `user endpoint requires authentication`() {
        // anonymous should be 401
        val anon = restTemplate.getForEntity("/api/user", String::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, anon.statusCode)

        // authenticated as apiuser
        val userResp = restTemplate.withBasicAuth("apiuser", "apipass")
            .getForEntity("/api/user", String::class.java)
        assertEquals(HttpStatus.OK, userResp.statusCode)
    }

    @Test
    fun `admin endpoint requires admin role`() {
        // apiuser (ROLE_USER) -> forbidden or unauthorized depending on config (here 403)
        val userResp = restTemplate.withBasicAuth("apiuser", "apipass")
            .getForEntity("/api/admin", String::class.java)
        assertTrue(userResp.statusCode == HttpStatus.FORBIDDEN || userResp.statusCode == HttpStatus.UNAUTHORIZED)

        // admin should succeed
        val adminResp = restTemplate.withBasicAuth("admin", "adminpass")
            .getForEntity("/api/admin", String::class.java)
        assertEquals(HttpStatus.OK, adminResp.statusCode)
    }
}
