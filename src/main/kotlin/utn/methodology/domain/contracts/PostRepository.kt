package utn.methodology.domain.contracts

import utn.methodology.domain.entities.Post

interface PostRepository {
    fun save(post: Post)
    fun findByOwnerId(ownerId: String): List<Post>
    fun findByFollows(followIds: List<String>): List<Post>
    fun deleteById(postId: String)
}
