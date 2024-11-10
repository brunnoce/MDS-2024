package utn.methodology.shared.mothers

import utn.methodology.domain.entities.User
import io.github.serpro69.kfaker.Faker
import java.util.UUID

class UserMother {

    companion object {
        private val faker = Faker()

        fun random(): User {
            return User(
                id = UUID.randomUUID().toString(),
                name = faker.name.name(),
                username = faker.southPark.characters(),
                email = faker.internet.email(),
                password = faker.random.randomString(10),
                followers = mutableListOf(),
                following = mutableListOf()
            )
        }
    }
}
