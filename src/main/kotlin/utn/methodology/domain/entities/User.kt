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
            val id = primitives["id"] ?: throw IllegalArgumentException("Id must not be null")
            val name = primitives["name"] ?: throw IllegalArgumentException("Name must not be null")
            val username = primitives["username"] ?: throw IllegalArgumentException("Username must not be null")
            val email = primitives["email"] ?: throw IllegalArgumentException("Email must not be null")
            val password = primitives["password"] ?: throw IllegalArgumentException("Password must not be null")

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