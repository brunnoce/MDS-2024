package utn.methodology.application.queryhandlers

import utn.methodology.application.queries.FindUserByUsernameQuery
import utn.methodology.infrastructure.persistence.MongoUserRepository
import io.ktor.server.plugins.*

class FindUserByUsernameHandler(
    private val userRepository: MongoUserRepository
) {

    fun handle(query: FindUserByUsernameQuery): Map<String, String> {

        // Encuentra al usuario por el username
        val user = userRepository.findByUsername(query.username)

        if (user == null) {
            throw NotFoundException("User with username: ${query.username} not found")
        }

        // Asumiendo que `user` es una lista, seleccionamos el primer resultado
        return user.toPrimitives()
    }
}
