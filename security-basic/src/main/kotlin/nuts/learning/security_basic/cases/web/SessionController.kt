package nuts.learning.security_basic.cases.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SessionController {

    @GetMapping("/session-expired")
    @ResponseBody
    fun sessionExpired(): String {
        return "세션이 만료되었습니다. 다른 곳에서 로그인되었을 수 있습니다."
    }

    @GetMapping("/session-invalid")
    @ResponseBody
    fun sessionInvalid(): String {
        return "세션이 유효하지 않습니다. 다시 로그인해주세요."
    }
}

