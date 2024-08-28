package utn.methodology.infrastructure.http.router

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import utn.methodology.domain.entities.User

private val users = mutableListOf(
    User(1,"Mati","matiMoglia","mati@gmail.com","mati123"),
    User(1,"Bruno","bruni","bruno@gmail.com","ce12345"),
)

fun Route.userRouting(){
    route("/user") {
        get{
            if (users.isNotEmpty()){
                call.respond(users)
            } else {
                call.respond("No se encuentran usuarios")
            }
        }
    }
}
