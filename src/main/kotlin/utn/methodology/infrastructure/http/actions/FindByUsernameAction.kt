package utn.methodology.infrastructure.http.actions

import utn.methodology.application.queryhandlers.FindUserByUsernameHandler
import utn.methodology.application.queries.FindUserByUsernameQuery
import utn.methodology.domain.entities.User

class FindUserByUsernameAction(
    private val handler: FindUserByUsernameHandler
) {
    fun execute(query: FindUserByUsernameQuery): User {
        return handler.handle(query)
    }
}