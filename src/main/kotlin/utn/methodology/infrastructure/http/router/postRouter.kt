package utn.methodology.infrastructure.http.router

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import utn.methodology.application.commandhandlers.CreatePostHandler
import utn.methodology.application.commandhandlers.DeletePostHandler
import utn.methodology.application.commandhandlers.PostValidationException
import utn.methodology.application.commands.CreatePostCommand
import utn.methodology.infrastructure.http.actions.CreatePostAction
import utn.methodology.infrastructure.http.actions.DeletePostAction
import utn.methodology.infrastructure.persistence.Repositories.MongoPostRepository
import utn.methodology.infrastructure.persistence.connectToMongoDB
import utn.methodology.infrastructure.persistence.MongoUserRepository

fun Application.postRouter() {
    val mongoDatabase = connectToMongoDB()
    val postRepository = MongoPostRepository(mongoDatabase)
    val userRepository = MongoUserRepository(mongoDatabase)

    val createPostHandler = CreatePostHandler(postRepository)

    routing {
        post("/posts") {
            try {
                println("Recibiendo solicitud para crear un post...")
                val request = call.receive<PostRequest>()
                println("Cuerpo de la solicitud: $request")

                if (userRepository.findOne(request.userId) != null) {
                    val command = CreatePostCommand(
                        userId = request.userId,
                        message = request.message
                    )

                    val createPostAction = CreatePostAction(createPostHandler)
                    createPostAction.execute(command)

                    println("Post creado: ${request.message}")
                    call.respond(HttpStatusCode.Created, mapOf("message" to "ok"))
                } else {
                    println("No se encontró el usuario, por lo que no se puede agregar un post")
                    call.respond(HttpStatusCode.BadRequest, "Usuario no encontrado")
                }

            } catch (e: PostValidationException) {
                println("Error de validación del post: ${e.message}")
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Datos de post inválidos")
            } catch (e: Exception) {
                println("Error inesperado: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Error al procesar la solicitud")
            }
        }

        get("/posts") {
            val userId = call.request.queryParameters["userId"]
            val order = call.request.queryParameters["order"]?.uppercase() ?: "DESC" // ORDEN
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10 // LIMITE DE POST
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0 // SKIP

            if (userId != null) {
                if (userId.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El 'userId' no puede estar en blanco"))
                    return@get
                }

                try {
                    val posts = postRepository.findByOwnerId(userId)
                    if (posts.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "No se encontraron posts para el userId: $userId"))
                    } else {
                        call.respond(HttpStatusCode.OK, posts)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error inesperado: ${e.localizedMessage}"))
                    println("Error al buscar posts para userId $userId: ${e.localizedMessage}")
                    e.printStackTrace()
                }
            } else {
                try {
                    val posts = postRepository.findAll(order, limit, offset)
                    call.respond(HttpStatusCode.OK, posts)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error inesperado: ${e.localizedMessage}"))
                    println("Error al obtener todos los posts: ${e.localizedMessage}")
                    e.printStackTrace()
                }
            }
        }

        get("/posts/{id}") {
            val postId = call.parameters["id"]

            if (postId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "El ID del post es requerido.")
                return@get
            }
            try {
                val post = postRepository.findById(postId)
                post?.let {
                    call.respond(HttpStatusCode.OK, it)
                } ?: throw NotFoundException("Post no encontrado con ID: $postId")
            } catch (ex: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, ex.message ?: "Post no encontrado")
            } catch (ex: Exception) {
                println("Error al buscar el post: ${ex.localizedMessage}")
                call.respond(HttpStatusCode.InternalServerError, "Error al procesar la solicitud: ${ex.localizedMessage}")
            }
        }

        delete("/posts/{id}") {
            val postId = call.parameters["id"]

            if (postId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "El ID del post es requerido.")
                return@delete
            }

            try {
                val deletePostAction = DeletePostAction(DeletePostHandler(postRepository))

                deletePostAction.execute(call.parameters)

                call.respond(HttpStatusCode.OK, mapOf("message" to "Post eliminado exitosamente"))
            } catch (ex: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, ex.message ?: "Post no encontrado")
            } catch (ex: Exception) {
                println("Error al eliminar el post: ${ex.localizedMessage}")
                call.respond(HttpStatusCode.InternalServerError, "Error al procesar la solicitud: ${ex.localizedMessage}")
            }
        }

        get("/posts/user/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "UserId es requerido")

            try {
                val user = userRepository.findOne(userId)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "Usuario no encontrado")
                    return@get
                }

                val followedUserIds = user.following
                if (followedUserIds.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent, "El usuario no sigue a nadie.")
                    return@get
                }

                println("Usuarios seguidos por $userId: $followedUserIds")
                val posts = postRepository.findPostsByUserIds(followedUserIds)
                if (posts.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent, "No se encontraron posts de usuarios seguidos.")
                } else {
                    call.respond(HttpStatusCode.OK, posts.map { it.toPrimitives() })
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error al obtener los posts: ${e.localizedMessage}")
            }
        }

    }
}

@Serializable
data class PostRequest(val userId: String, val message: String)