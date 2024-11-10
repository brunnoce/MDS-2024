package utn.methodology.unit.application

import utn.methodology.application.commandhandlers.CreatePostHandler
import utn.methodology.application.commandhandlers.PostValidationException
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
        mockUserRepository.save(user)

        val post = PostMother.withContent(user.id, "Este es un mensaje de prueba que no excede los 500 caracteres.")

        // Act
        sut.createPost(post.userId, post.message)

        // Assertions
        val posts = mockPostRepository.findByOwnerId(user.id)
        assert(posts.size == 1) { "El post debería haberse guardado." }
        assert(posts[0].message == post.message) { "El contenido del post no coincide." }
        assert(posts[0].userId == post.userId) { "El ID del usuario no coincide." }
    }

    @Test
    fun `create_post_should_returns_400`() {
        // Arrange
        val user = UserMother.random()
        mockUserRepository.save(user)

        val longPost = PostMother.withContent(user.id, "a".repeat(501)) // +500 caracteres

        // Act & Assert
        assertFailsWith<PostValidationException> {
            sut.createPost(longPost.userId, longPost.message)
        }
    }
}
