package utn.methodology.infrastructure.http.router

import utn.methodology.application.commands.CreateUserCommand
import utn.methodology.application.queryhandlers.FindUserByUsernameHandler
import utn.methodology.application.queries.FindUserByUsernameQuery
import utn.methodology.application.commandhandlers.CreateUserHandler
import utn.methodology.infrastructure.persistence.MongoUserRepository
import utn.methodology.infrastructure.persistence.connectToMongoDB
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import utn.methodology.application.commandhandlers.DeletePostHandler
import utn.methodology.application.commandhandlers.FollowUserHandler
import utn.methodology.application.queries.FindUserByIdQuery
import utn.methodology.application.queryhandlers.FindUserByIdHandler
import utn.methodology.infrastructure.http.actions.*

fun Application.userRouter() {
    val mongoDatabase = connectToMongoDB()

    val userMongoUserRepository = MongoUserRepository(mongoDatabase)

    val createUserAction = CreateUserAction(CreateUserHandler(userMongoUserRepository))
    val findUserByUsernameAction = FindUserByUsernameAction(FindUserByUsernameHandler(userMongoUserRepository))
    val findUserByIdAction = FindUserByIdAction(FindUserByIdHandler(userMongoUserRepository))
    val followUserAction = FollowUserAction(FollowUserHandler(userMongoUserRepository))

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
                    val user = findUserByUsernameAction.execute(query)
                    call.respond(HttpStatusCode.OK, user.toPrimitives())
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to e.localizedMessage))
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

        get("/users/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El ID no puede estar vacío"))
                return@get
            }
            try {
                val query = FindUserByIdQuery(id).validate()

                val user = findUserByIdAction.execute(query)

                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuario no encontrado"))
                } else {
                    call.respond(HttpStatusCode.OK, user.toPrimitives())
                }
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.localizedMessage))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error inesperado: ${e.localizedMessage}"))
                println("Error al buscar el usuario por ID: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }

        post("/users/follow") {
            val params = call.receive<Map<String, String>>()

            val followerId = params["followerId"]
            val followingId = params["followingId"]

            if (followerId.isNullOrBlank() || followingId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Los IDs de los usuarios son requeridos.")
                return@post
            }

            try {
                followUserAction.execute(followerId, followingId)
                call.respond(HttpStatusCode.OK, "Usuario seguido correctamente.")
            } catch (ex: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "Error al intentar seguir al usuario.")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error inesperado: ${ex.message}")
                println("Error al seguir al usuario: ${ex.localizedMessage}")
                ex.printStackTrace()
            }
        }
    }
}
