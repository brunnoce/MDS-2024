package utn.methodology.unit.application

import utn.methodology.application.commandhandlers.CreatePostHandler
import utn.methodology.application.commandhandlers.PostValidationException
import utn.methodology.domain.entities.Post
import utn.methodology.shared.mocks.MockPostRepository
import utn.methodology.shared.mocks.MockUserRepository
import utn.methodology.shared.mothers.PostMother
import utn.methodology.shared.mothers.UserMother
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertFailsWith

class CreatePostHandlerTest {

    private val mockPostRepository = MockPostRepository()
    private val mockUserRepository = MockUserRepository()
    private lateinit var sut: CreatePostHandler

    @BeforeTest
    fun beforeEach() {
        mockPostRepository.clean()
        mockUserRepository.clean()
        sut = CreatePostHandler(mockPostRepository)
    }

    @Test
    fun `create_post_should_returns_201`() {
        // Arrange
        val user = UserMother.random()
        val content = "Este es un mensaje de prueba que no excede los 500 caracteres."

        mockUserRepository.save(user)  // Guardamos el usuario en el repositorio

        // Act
        val post = sut.createPost(user.id, content)

        // Assertions
        val posts = mockPostRepository.findByOwnerId(user.id)
        assert(posts.size == 1) { "El post debería haberse guardado." }
        assert(posts[0].message == content) { "El contenido del post no coincide." }
        assert(posts[0].userId == user.id) { "El ID del usuario no coincide." }
    }

    @Test
    fun `create_post_should_returns_400`() {
        // Arrange
        val user = UserMother.random()
        val longContent = "a".repeat(501)  // Genera un contenido de más de 500 caracteres

        mockUserRepository.save(user)  // Guardamos el usuario en el repositorio

        // Act & Assert
        assertFailsWith<PostValidationException> {
            sut.createPost(user.id, longContent)  // Debería lanzar la excepción por contenido demasiado largo
        }
    }
}
