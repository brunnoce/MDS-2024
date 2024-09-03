package utn.methodology.infrastructure.http.actions

import utn.methodology.application.queryhandlers.FindUserByUsernameHandler
import utn.methodology.application.queries.FindUserByUsernameQuery
import io.ktor.server.application.*
import io.ktor.server.response.*

class FindUserByUsernameAction(
    private val handler: FindUserByUsernameHandler
) {

    fun execute(query: FindUserByUsernameQuery): Map<String, String> {
        return handler.handle(query)
    }
}
