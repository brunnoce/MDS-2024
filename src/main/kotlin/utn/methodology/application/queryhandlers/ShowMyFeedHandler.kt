package utn.methodology.application.queryhandlers

import utn.methodology.application.queries.ShowMyFeedQuery
import utn.methodology.domain.contracts.PostRepository
import utn.methodology.domain.contracts.UserRepository
import utn.methodology.domain.entities.Post
import io.ktor.server.plugins.*

class ShowMyFeedHandler(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) {

    fun handle(query: ShowMyFeedQuery): List<Post> {
        val user = userRepository.findOne(query.userId) ?: throw NotFoundException("Usuario no encontrado")

        val followedUserIds = user.following
        if (followedUserIds.isEmpty()) {
            return emptyList()
        }

        return postRepository.findPostsByUserIds(followedUserIds).sortedByDescending { it.createdAt }
    }
}
