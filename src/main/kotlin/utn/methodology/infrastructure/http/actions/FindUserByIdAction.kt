package utn.methodology.infrastructure.http.actions

import utn.methodology.application.queryhandlers.FindUserByIdHandler
import utn.methodology.application.queries.FindUserByIdQuery
import utn.methodology.domain.entities.User

class FindUserByIdAction(
    private val handler: FindUserByIdHandler
) {
    fun execute(query: FindUserByIdQuery): User? {
        return handler.handle(query)
    }
}