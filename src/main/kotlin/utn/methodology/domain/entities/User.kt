package utn.methodology.domain.entities

import kotlinx.serialization.Serializable
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.Primitive

@Serializable
data class User(private val id: String,
                private var name: String,
                private var username: String,
                private val email: String,
                private val password: String)
{
   companion object {
        fun fromPrimitives(primitives: Map<String, String>): User {
            val user = User(
                primitives["id"] as String,
                primitives["name"] as String,
                primitives["username"] as String,
                primitives["email"] as String,
                primitives["password"] as String
            );

            return user
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