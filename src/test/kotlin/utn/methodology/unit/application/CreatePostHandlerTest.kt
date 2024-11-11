package utn.methodology.unit.application

import utn.methodology.application.commandhandlers.CreatePostHandler
import utn.methodology.application.commandhandlers.PostValidationException
import utn.methodology.application.commands.CreatePostCommand
import utn.methodology.shared.mocks.MockPostRepository
import utn.methodology.shared.mocks.MockUserRepository
import utn.methodology.shared.mothers.PostMother
import utn.methodology.shared.mothers.UserMother
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CreatePostHandlerTest {

    private val mockPostRepository = MockPostRepository()
    private val mockUserRepository = MockUserRepository()
    private lateinit var sut: CreatePostHandler

    @BeforeTest
    fun beforeEach() {
        mockPostRepository.clean()
        mockUserRepository.clean()
        sut = CreatePostHandler(mockPostRepository, mockUserRepository)
    }

    @Test
    fun `create_post_should_returns_201`() {
        // Arrange
        val user = UserMother.random()
        mockUserRepository.save(user)

        // Usamos PostMother.random para generar un post aleatorio
        val post = PostMother.random(user.id)
        val command = CreatePostCommand(userId = post.userId, message = post.message)

        // Act
        val result = sut.handle(command)

        // Assert
        val savedPosts = mockPostRepository.findByOwnerId(user.id)
        assert(savedPosts.size == 1) { "El post debería haberse guardado correctamente." }
        val savedPost = savedPosts.first()
        assertNotNull(savedPost) { "El post guardado no debería ser nulo." }
        assert(savedPost.message == post.message) { "El contenido del post no coincide." }
        assert(savedPost.userId == post.userId) { "El ID del usuario no coincide." }
        assert(result.id == savedPost.id) { "El ID del post retornado no coincide con el guardado." }
    }

    @Test
    fun `create_post_should_returns_400_when_message_exceeds_500_characters`() {
        // Arrange
        val user = UserMother.random()
        mockUserRepository.save(user)

        val longMessage = "a".repeat(501)
        val command = CreatePostCommand(userId = user.id, message = longMessage)

        // Act & Assert
        assertFailsWith<PostValidationException> {
            sut.handle(command)
        }
    }

    @Test
    fun `create_post_should_returns_400_when_userId_is_invalid`() {
        // Arrange
        val invalidCommand = CreatePostCommand(userId = "invalid-user-id", message = "Mensaje válido")

        // Act & Assert
        assertFailsWith<IllegalArgumentException> {
            sut.handle(invalidCommand)
        }
    }

    @Test
    fun `create_post_should_handle_empty_repository`() {
        // Arrange
        val user = UserMother.random()
        val command = CreatePostCommand(userId = user.id, message = "Nuevo mensaje")

        // Act
        val result = sut.handle(command)

        // Assert
        assertNotNull(result) { "El post creado no debería ser nulo incluso si la base de datos estaba vacía." }
    }

    @Test
    fun `create_post_should_returns_400_when_message_is_empty`() {
        // Arrange
        val user = UserMother.random()
        mockUserRepository.save(user)
        val command = CreatePostCommand(userId = user.id, message = "")

        // Act & Assert
        assertFailsWith<PostValidationException> {
            sut.handle(command)
        }
    }
}
