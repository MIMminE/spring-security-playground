package nuts.learning.security_oauth2.oauth2_client.google.v3.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByName(userName: String): Boolean
    fun findByName(userName: String): User?
}