package utn.methodology.infrastructure.http.router

import utn.methodology.application.commands.CreateUserCommand
import utn.methodology.application.queryhandlers.FindUserByUsernameHandler
import utn.methodology.application.queries.FindUserByUsernameQuery
import utn.methodology.infrastructure.http.actions.CreateUserAction
import utn.methodology.application.commandhandlers.CreateUserHandler
import utn.methodology.infrastructure.http.actions.FindUserByUsernameAction
import utn.methodology.infrastructure.persistence.MongoUserRepository
import utn.methodology.infrastructure.persistence.connectToMongoDB
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRouter() {
    val mongoDatabase = connectToMongoDB()

    val userMongoUserRepository = MongoUserRepository(mongoDatabase)

    val createUserAction = CreateUserAction(CreateUserHandler(userMongoUserRepository))
    val findUserByUsernameAction = FindUserByUsernameAction(FindUserByUsernameHandler(userMongoUserRepository))

    routing {
        get("/") {
            call.respondText("Hello, world!")
        }

        post("/users") {
            val body = call.receive<CreateUserCommand>()
            createUserAction.execute(body)
            call.respond(HttpStatusCode.Created, mapOf("message" to "ok"))
        }

        get("/users") {
            val username = call.request.queryParameters["username"]
            if (username != null) {
                // Handle search by username
                try {
                    val query = FindUserByUsernameQuery(username).validate()
                    val result = findUserByUsernameAction.execute(query)
                    call.respond(HttpStatusCode.OK, result)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.localizedMessage))
                    println("Error al buscar el usuario por username: ${e.localizedMessage}")
                    e.printStackTrace()
                }
            } else {
                // Handle fetch all users if no username is provided
                try {
                    val users = userMongoUserRepository.findAll()
                    call.respond(HttpStatusCode.OK, users.map { it.toPrimitives() })
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.localizedMessage))
                    println("Error al obtener los usuarios: ${e.localizedMessage}")
                    e.printStackTrace()
                }
            }
        }
    }
}