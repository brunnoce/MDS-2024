package utn.methodology.domain.contracts

import utn.methodology.domain.entities.User

interface UserRepository {
    fun save(user: User)
    fun findOne(id: String): User?
    fun findByUsername(username: String): User?
    fun delete(user: User)
}
