package utn.methodology.shared.mocks

import utn.methodology.domain.contracts.UserRepository
import utn.methodology.domain.entities.User

class MockUserRepository : UserRepository {

    private val users = mutableListOf<User>()

    override fun save(user: User) {
        users.removeIf { it.id == user.id }
        users.add(user)
    }

    override fun findOne(id: String): User? {
        return users.find { it.id == id }
    }

    override fun findByUsername(username: String): User? {
        return users.find { it.username.equals(username, ignoreCase = true) }
    }

    override fun delete(user: User) {
        users.removeIf { it.id == user.id }
    }

    fun clean() {
        users.clear()
    }
}
