package utn.methodology.infrastructure.http.actions

import utn.methodology.application.queryhandlers.FindUserByUsernameHandler
import utn.methodology.application.queries.FindUserByUsernameQuery

class FindUserByUsernameAction(
    private val handler: FindUserByUsernameHandler
) {

    fun execute(query: FindUserByUsernameQuery): List<Map<String, String>> {
        return handler.handle(query)
    }
}

//class FindUserByUsernameAction(
//    private val handler: FindUserByUsernameHandler
//) {
//
//    fun execute(query: FindUserByUsernameQuery): Map<String, String> {
//        return handler.handle(query)
//    }
//}
//