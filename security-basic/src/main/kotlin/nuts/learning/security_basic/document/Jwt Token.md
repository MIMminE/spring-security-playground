# JWT 토큰 프로바이더(JwtTokenProvider) 정리

## 1. 개념 및 역할

- **JWT 토큰 프로바이더**는 JWT(Json Web Token)를 생성, 검증, 파싱하는 컴포넌트이다.
- 인증이 필요한 REST API에서, 사용자의 인증 정보를 담은 JWT를 발급하고,  
  클라이언트가 보낸 JWT의 유효성을 검사하여 인증/인가 처리를 담당한다.

## 2. 주요 기능

1. **토큰 생성**
    - 로그인 성공 시 사용자 정보(주로 username, roles 등)를 담아 JWT를 생성
    - 비밀키로 서명하여 위변조를 방지

2. **토큰 파싱**
    - 클라이언트가 보낸 JWT에서 사용자 정보(예: username, roles)를 추출

3. **토큰 검증**
    - 토큰의 서명, 만료 여부 등 유효성 검사

## 3. JWT의 주요 용어

- **서브젝트(subject)**:  
  토큰의 주체(누구에 대한 토큰인지, 예: username)

- **클레임(claim)**:  
  토큰에 담기는 정보(키-값 쌍, 예: roles, email 등)

- **싸인(sign, 서명)**:  
  비밀키로 토큰 전체에 대해 암호학적 서명을 하여 위변조 방지

## 4. 실제 코드 예시 (`JwtTokenProvider.kt`)

```kotlin
package nuts.learning.security_basic.cases.rest.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${security.jwt.secret}") private val secret: String,
    @Value("\${security.jwt.expiration:3600000}") private val expirationMillis: Long
) {
    private val secretKey = Keys.hmacShaKeyFor(secret.toByteArray())
    private val jwtParser = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()

    // 토큰 생성
    fun createToken(username: String, roles: List<String>): String =
        Jwts.builder()
            .setSubject(username) // 서브젝트
            .claim("roles", roles) // 클레임
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationMillis))
            .signWith(secretKey) // 싸인(서명)
            .compact()

    // 토큰에서 username 추출
    fun getUsername(token: String): String =
        jwtParser.parseClaimsJws(token).body.subject

    // 토큰 유효성 검증
    fun validateToken(token: String): Boolean =
        try {
            val claims = jwtParser.parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
}
```

서명(Signature)은 JWT의 무결성과 신뢰성을 보장하는 암호학적 처리이다.

- JWT는 `헤더(Header)`, `페이로드(Payload)`, `서명(Signature)` 세 부분으로 구성된다.
- 서명은 비밀키(혹은 공개 개인키 쌍)을 사용해 헤더와 페이로드를 암호학적으로 해시한 값이다.
- 서버는 비밀키로 서명하고, 클라이언트가 보낸 JWT의 서명을 다시 검증하여 토큰이 위변조되지 않았음을 확인한다.

즉, 서명 덕분에 토큰의 내용이 중간에 변조되었는지, 신뢰할 수 있는 서버에서 발급된 것인지 확인할 수 있다.  
비밀키가 노출되지 않는 한, 서명된 JWT는 안전하게 신뢰할 수 있다.

현재 코드에서는 `io.jsonwebtoken` 라이브러리의 `Keys.hmacShaKeyFor(secret.toByteArray())`를 사용하여 HMAC\-SHA 알고리즘\(HMAC\-SHA256 등\) 기반의
대칭키 서명을 사용한다.  
즉, 비밀키\(`secret`\)로 JWT의 헤더와 페이로드를 HMAC 방식으로 해시하여 서명\(Signature\)을 생성하고,  
토큰 검증 시에도 같은 비밀키로 서명의 유효성을 확인한다.

이 방식은 `대칭키 기반의 HMAC-SHA 서명`이다.