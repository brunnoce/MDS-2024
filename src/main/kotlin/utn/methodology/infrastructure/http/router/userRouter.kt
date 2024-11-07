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
                if (username.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El username no puede estar en blanco"))
                    return@get
                }
                try {
                    val query = FindUserByUsernameQuery(username).validate()
                    val result = findUserByUsernameAction.execute(query)

                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "No se encontró el siguiente username: $username"))
                    } else {
                        call.respond(HttpStatusCode.OK, result)
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.localizedMessage))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error inesperado: ${e.localizedMessage}"))
                    println("Error al buscar el usuario por username: ${e.localizedMessage}")
                    e.printStackTrace()
                }
            } else {
                try {
                    val users = userMongoUserRepository.findAll()
                    call.respond(HttpStatusCode.OK, users.map { it.toPrimitives() })
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error inesperado: ${e.localizedMessage}"))
                    println("Error al obtener los usuarios: ${e.localizedMessage}")
                    e.printStackTrace()
                }
            }

        }
        post("/users/follow") {
            val body = call.receive<Map<String, String>>()
            val followerId = body["followerId"]
            val followingId = body["followingId"]

            if (followerId == null || followingId == null) {
                call.respond(HttpStatusCode.BadRequest, "Ambos 'followerId' y 'followingId' son requeridos.")
                return@post
            }

            try {
                val user = userMongoUserRepository.findOne(followerId)
                val userToFollow = userMongoUserRepository.findOne(followingId)

                if (user == null || userToFollow == null) {
                    call.respond(HttpStatusCode.NotFound, "Uno o ambos usuarios no fueron encontrados.")
                    return@post
                }

                // Agregar el seguidor y el seguido
                userMongoUserRepository.addFollower(followingId, followerId)
                userMongoUserRepository.addFollowing(followerId, followingId)

                call.respond(HttpStatusCode.OK, "El usuario ha seguido correctamente.")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error al seguir al usuario: ${e.localizedMessage}")
            }
        }


    }
}
