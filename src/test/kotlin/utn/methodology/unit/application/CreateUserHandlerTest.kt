package utn.methodology.unit.application

import utn.methodology.application.commandhandlers.CreateUserHandler
import utn.methodology.application.commands.CreateUserCommand
import utn.methodology.shared.mocks.MockUserRepository
import utn.methodology.shared.mothers.UserMother
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CreateUserHandlerTest {

    private val mockUserRepository = MockUserRepository()
    private lateinit var sut: CreateUserHandler

    @BeforeTest
    fun setUp() {
        mockUserRepository.clean()
        sut = CreateUserHandler(mockUserRepository)
    }

    @Test
    fun `create_user_should_returns_201`() {
        val user = UserMother.random()
        val command = CreateUserCommand(
            name = user.name,
            username = user.username,
            email = user.email,
            password = user.getPassword()
        )

        sut.handle(command)

        val savedUser = mockUserRepository.findByUsername(user.username)
        assertNotNull(savedUser, "El usuario debería haber sido guardado en el repositorio.")
        assert(savedUser.email == command.email) { "Los emails no coinciden" }
    }

    @Test
    fun `create_user_should_persist_followers_and_following`() {
        val user = UserMother.random()
        val followers = mutableListOf("follower1", "follower2")
        val following = mutableListOf("following1", "following2")

        val command = CreateUserCommand(
            name = user.name,
            username = user.username,
            email = user.email,
            password = user.getPassword(),
            followers = followers,
            following = following
        )

        sut.handle(command)

        val savedUser = mockUserRepository.findByUsername(user.username)
        assertNotNull(savedUser)
        assert(savedUser.followers == followers) { "Los seguidores no fueron guardados correctamente." }
        assert(savedUser.following == following) { "Los seguidos no fueron guardados correctamente." }
    }

    @Test
    fun `create_user_should_fail_when_username_already_exists`() {
        val user = UserMother.random()
        val command = CreateUserCommand(
            name = user.name,
            username = user.username,
            email = user.email,
            password = user.getPassword()
        )

        mockUserRepository.save(user)

        assertFailsWith<IllegalArgumentException>("Debería fallar si el username ya existe.") {
            sut.handle(command)
        }
    }

    @Test
    fun `create_user_should_fail_when_fields_are_blank`() {
        val command = CreateUserCommand(
            name = "",
            username = "",
            email = "",
            password = ""
        )

        assertFailsWith<IllegalArgumentException>("Todos los campos deben ser válidos.") {
            sut.handle(command)
        }
    }
}