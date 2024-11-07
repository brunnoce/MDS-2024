package utn.methodology.infrastructure.http.actions

import utn.methodology.application.queryhandlers.FindUserByUsernameHandler
import utn.methodology.application.queries.FindUserByUsernameQuery

class FindUserByUsernameAction(
    private val handler: FindUserByUsernameHandler
) {

    fun execute(query: FindUserByUsernameQuery): List<Unit> {
        return handler.handle(query)
    }
}
