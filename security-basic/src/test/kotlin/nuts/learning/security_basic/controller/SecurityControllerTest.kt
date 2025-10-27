package nuts.learning.security_basic.controller

import nuts.learning.security_basic.config.SecurityConfig
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(BasicController::class)
@Import(SecurityConfig::class)
class SecurityControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `공개 엔드포인트는 인증 없이 접근 가능`() {
        mockMvc.perform(get("/api/public"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("누구나 접근 가능합니다")))
    }

    @Test
    fun `인증되지 않은 사용자가 사용자 엔드포인트 접근 시 401`() {
        mockMvc.perform(get("/api/user"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `인증된 사용자로 공개 엔드포인트 접근`() {
        mockMvc.perform(get("/api/public"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("누구나 접근 가능합니다")))
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `USER 역할로 사용자 엔드포인트 접근`() {
        mockMvc.perform(get("/api/user"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("인증된 사용자만 접근 가능합니다")))
    }

    @Test
    fun `HTTP Basic 인증으로 사용자 엔드포인트 접근`() {
        mockMvc.perform(
            get("/api/user")
                .with(httpBasic("admin", "password123"))
        )
            .andExpect(status().isOk)
    }
}