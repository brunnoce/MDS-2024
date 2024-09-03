package utn.methodology.domain.entities

import kotlinx.serialization.Serializable
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.Primitive

@Serializable
data class User(
    private val id: String,
    private var name: String,
    private var username: String,
    private val email: String,
    private val password: String)
{
    companion object {
        fun fromPrimitives(primitives: Map<String, String?>): User {
            val id = primitives["id"] ?: throw IllegalArgumentException("El ID no puede ser nulo/null")
            val name = primitives["name"] ?: throw IllegalArgumentException("El name no puede ser nulo/null")
            val username = primitives["username"] ?: throw IllegalArgumentException("El username no puede ser nulo/null")
            val email = primitives["email"] ?: throw IllegalArgumentException("El email no puede ser nulo/null")
            val password = primitives["password"] ?: throw IllegalArgumentException("La password no puede ser nulo/null")

            return User(id, name, username, email, password)
        }
    }


    fun getId(): String {
        return this.id;
    }

    fun update(name: String, username: String) {
        this.name = name;
        this.username = username;
    }

    fun toPrimitives(): Map<String, String> {
        return mapOf(
            "id" to this.id,
            "name" to this.name,
            "username" to this.username,
            "email" to this.email,
            "password" to this.password
        )
    }

}