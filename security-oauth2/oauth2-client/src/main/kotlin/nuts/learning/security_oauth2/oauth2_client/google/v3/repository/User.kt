package nuts.learning.security_oauth2.oauth2_client.google.v3.repository

import jakarta.persistence.*
import lombok.Builder
import lombok.NoArgsConstructor

// OIDC 인증 과정에서 유저 정보 영속화를 위한 엔티티 클래스
@Entity
@Table(name = "users", schema = "public")
@Builder
@NoArgsConstructor
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var provider: String = "",

    @Column(name = "provider_id", nullable = false)
    var providerId: String = "",

    @Column(nullable = true)
    var email: String? = null,

    @Column(nullable = true)
    var name: String? = null,

    @Column(nullable = true)
    var picture: String? = null,

    @Column(nullable = false)
    var roles: List<String> = mutableListOf()

) {
}