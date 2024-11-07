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
            id = UUID.randomUUID().toString(),
            name = command.name,
            username = command.username,
            email = command.email,
            password = command.password,
            followers = mutableListOf(),
            following = mutableListOf()
        )

        userRepository.save(user)
    }
}
