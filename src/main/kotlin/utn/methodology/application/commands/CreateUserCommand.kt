package utn.methodology.application.commands

import kotlinx.serialization.Serializable

@Serializable()
data class CreateUserCommand(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val followers: MutableList<String> = mutableListOf(),
    val following: MutableList<String> = mutableListOf()
) {
    fun validate(): CreateUserCommand {
        checkNotNull(name) { throw IllegalArgumentException("Name debe estar definido") }
        checkNotNull(username) { throw IllegalArgumentException("Username debe estar definido") }
        checkNotNull(email) { throw IllegalArgumentException("Email debe estar definido") }
        checkNotNull(password) { throw IllegalArgumentException("Password debe estar definido") }
        return this
    }
}