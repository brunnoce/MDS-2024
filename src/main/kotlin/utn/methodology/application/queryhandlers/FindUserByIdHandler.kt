package utn.methodology.application.queryhandlers

import io.ktor.server.plugins.*
import utn.methodology.application.queries.FindUserByIdQuery
import utn.methodology.domain.entities.User
import utn.methodology.infrastructure.persistence.MongoUserRepository

class FindUserByIdHandler(
    private val userRepository: MongoUserRepository
) {
    fun handle(query: FindUserByIdQuery): User {
        val user = userRepository.findOne(query.id)
            ?: throw NotFoundException("Usuario no encontrado con username: ${query.id}")

        return user
    }
}