package utn.methodology.application.queryhandlers

import utn.methodology.application.queries.FindUserByUsernameQuery
import utn.methodology.infrastructure.persistence.MongoUserRepository
import io.ktor.server.plugins.*
import utn.methodology.domain.entities.User

class FindUserByUsernameHandler(
    private val userRepository: MongoUserRepository
) {
    fun handle(query: FindUserByUsernameQuery): User {
        val user = userRepository.findByUsername(query.username)
            ?: throw NotFoundException("Usuario no encontrado con username: ${query.username}")

        return user
    }
}

