package utn.methodology.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    var name: String,
    var username: String,
    val email: String,
    private val password: String,
    var followers: List<String> = mutableListOf(),
    var following: List<String> = mutableListOf()
) {
    fun getPassword() = password

    companion object {
        fun fromPrimitives(primitives: Map<String, String?>): User {
            println("Deserializando User: $primitives")

            val id = primitives["id"] ?: throw IllegalArgumentException("El ID no puede ser nulo/null")
            val name = primitives["name"] ?: throw IllegalArgumentException("El name no puede ser nulo/null")
            val username = primitives["username"] ?: throw IllegalArgumentException("El username no puede ser nulo/null")
            val email = primitives["email"] ?: throw IllegalArgumentException("El email no puede ser nulo/null")
            val password = primitives["password"] ?: throw IllegalArgumentException("La password no puede ser nulo/null")

            return User(id, name, username, email, password)
        }
    }

    fun toPrimitives(): Map<String, String> {
        return mapOf(
            "id" to this.id,
            "name" to this.name,
            "username" to this.username,
            "email" to this.email,
            "password" to this.password,
        )
    }
}