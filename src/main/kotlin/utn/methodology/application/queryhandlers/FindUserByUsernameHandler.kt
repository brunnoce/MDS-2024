package utn.methodology.application.queryhandlers

import utn.methodology.application.queries.FindUserByUsernameQuery
import utn.methodology.infrastructure.persistence.MongoUserRepository
import io.ktor.server.plugins.*

class FindUserByUsernameHandler(
    private val userRepository: MongoUserRepository
) {
    fun handle(query: FindUserByUsernameQuery): List<Unit> {

        val users = userRepository.findByUsernameContains(query.username)

        if (users.isEmpty()) {
            throw NotFoundException("No se encontraron usuarios con username que contenga: ${query.username}")
        }

        return users.map{}
    }
}


