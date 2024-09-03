package utn.methodology.application.commandhandlers

import java.util.*
import utn.methodology.application.commands.CreateUserCommand
import utn.methodology.domain.entities.User
import utn.methodology.infrastructure.persistence.MongoUserRepository
import java.util.UUID

class CreateUserHandler(
    private val userRepository: MongoUserRepository
) {
    fun handle(command: CreateUserCommand) {
        val user = User(
            UUID.randomUUID().toString(),
            command.name,
            command.username,
            command.email,
            command.password
        )

        userRepository.save(user)
    }
}