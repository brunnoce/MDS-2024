package utn.methodology.infrastructure.http.actions

import utn.methodology.application.queryhandlers.ShowMyFeedHandler
import utn.methodology.application.queries.ShowMyFeedQuery
import utn.methodology.domain.entities.Post

class ShowMyFeedAction(private val handler: ShowMyFeedHandler) {

    fun execute(query: ShowMyFeedQuery): List<Post> {
        query.validate()
        return handler.handle(query)
    }
}
