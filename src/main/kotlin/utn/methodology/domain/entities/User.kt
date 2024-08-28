package utn.methodology.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int, val name: String, val username: String, val email: String, val password: String)