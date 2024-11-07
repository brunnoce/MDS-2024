package utn.methodology.infrastructure.http.router

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import utn.methodology.application.commandhandlers.CreatePostHandler
import utn.methodology.application.commandhandlers.PostValidationException
import utn.methodology.infrastructure.persistence.Repositories.MongoPostRepository
import utn.methodology.infrastructure.persistence.connectToMongoDB
import utn.methodology.infrastructure.persistence.MongoUserRepository

fun Application.postRouter() {
    val mongoDatabase = connectToMongoDB() // Conexión a la base de datos
    val postRepository = MongoPostRepository(mongoDatabase) // Repositorio de posts
    val userRepository = MongoUserRepository(mongoDatabase) // Repositorio de usuarios

    val createPostHandler = CreatePostHandler(postRepository)

    routing {
        post("/posts") {
            try {
                println("Recibiendo solicitud para crear un post...")
                val request = call.receive<PostRequest>()
                println("Cuerpo de la solicitud: $request")

                if (userRepository.findOne(request.userId) != null) {
                    val post = createPostHandler.createPost(request.userId, request.message)
                    println("Post creado: $post")
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

            if (userId != null) {
                if (userId.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El 'userId' no puede estar en blanco"))
                    return@get
                }

                try {
                    val posts = postRepository.findByUserId(userId)
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
                    val posts = postRepository.findAll()
                    call.respond(HttpStatusCode.OK, posts)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error inesperado: ${e.localizedMessage}"))
                    println("Error al obtener todos los posts: ${e.localizedMessage}")
                    e.printStackTrace()
                }
            }
        }

        delete("/posts/{id}") {
            val postId = call.parameters["id"]

            if (postId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "El ID del post es requerido.")
                return@delete
            }

            try {
                val post = postRepository.findById(postId)
                if (post == null) {
                    call.respond(HttpStatusCode.NotFound, "Post no encontrado")
                    return@delete
                }

                postRepository.deleteById(postId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Post eliminado exitosamente"))

            } catch (ex: Exception) {
                println("Error al eliminar el post: ${ex.localizedMessage}")
                call.respond(HttpStatusCode.InternalServerError, "Error al procesar la solicitud: ${ex.localizedMessage}")
            }
        }

        get("/posts/users/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "UserId es requerido")

            try {
                val user = userRepository.findOne(userId)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "Usuario no encontrado")
                    return@get
                }

                println("Usuarios seguidos por $userId: ${user.following}")
                val posts = postRepository.findPostsByUserIds(user.following)

                if (posts.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent, "No se encontraron posts de usuarios seguidos.")
                    return@get
                }

                call.respond(HttpStatusCode.OK, posts.map { it.toPrimitives() })
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error al obtener los posts: ${e.localizedMessage}")
            }
        }


    }
}

@Serializable
data class PostRequest(val userId: String, val message: String)
