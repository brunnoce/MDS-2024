package utn.methodology.shared.mocks

import utn.methodology.domain.contracts.PostRepository
import utn.methodology.domain.entities.Post

class MockPostRepository : PostRepository {
    private val posts = mutableListOf<Post>()

    override fun save(post: Post) {
        posts.removeIf { it.id == post.id }
        posts.add(post)
    }

    override fun findByOwnerId(ownerId: String): List<Post> {
        return posts.filter { it.userId == ownerId }
    }

    override fun findByFollows(followIds: List<String>): List<Post> {
        return posts.filter { followIds.contains(it.userId) }
    }

    override fun deleteById(postId: String) {
        val postToRemove = posts.find { it.id.toString() == postId }
        if (postToRemove != null) {
            posts.remove(postToRemove)
        } else {
            throw Exception("Post not found with ID: $postId")
        }
    }

    fun clean() {
        posts.clear()
    }
}
