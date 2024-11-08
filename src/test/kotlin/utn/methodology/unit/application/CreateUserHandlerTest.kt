package utn.methodology.unit.application

import utn.methodology.application.commandhandlers.CreateUserHandler
import utn.methodology.application.commands.CreateUserCommand
import utn.methodology.shared.mocks.MockUserRepository
import utn.methodology.shared.mothers.UserMother
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith

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
    fun `create_user_should_returns_400`() {
        val invalidCommand = CreateUserCommand(
            name = "",
            username = "",
            email = "",
            password = ""
        )

        assertFailsWith<IllegalArgumentException> {
            sut.handle(invalidCommand)
        }
    }
}
