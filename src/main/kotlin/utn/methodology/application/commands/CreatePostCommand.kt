package utn.methodology.application.commands

import utn.methodology.application.commandhandlers.PostValidationException

data class CreatePostCommand(
    val userId: String,
    val message: String
) {
    fun validate(): CreatePostCommand {
        checkNotNull(userId) { throw IllegalArgumentException("UserID no debe ser nulo") }
        checkNotNull(message) { throw PostValidationException("Username no debe ser nulo") }

        // Validar que el mensaje no esté vacío
        if (message.isBlank()) {
            throw PostValidationException("El mensaje no puede estar vacío")
        }

        require(message.length <= 500) { throw PostValidationException("El mensaje no puede exceder los 500 caracteres.") }

        return this
    }
}