package utn.methodology.application.queryhandlers

import utn.methodology.application.queries.FindUserByUsernameQuery
import utn.methodology.infrastructure.persistence.MongoUserRepository
import io.ktor.server.plugins.*

class FindUserByUsernameHandler(
    private val userRepository: MongoUserRepository
) {

    fun handle(query: FindUserByUsernameQuery): Map<String, String> {

        val user = userRepository.findByUsername(query.username)

        if (user == null) {
            throw NotFoundException("Usuario con username: ${query.username} no encontrado")
        }

        return user.toPrimitives()
    }
}
